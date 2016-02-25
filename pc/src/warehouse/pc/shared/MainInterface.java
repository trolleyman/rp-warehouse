package warehouse.pc.shared;

import java.util.ArrayList;

import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.job.Drop;
import warehouse.pc.job.Item;
import warehouse.pc.job.ItemList;
import warehouse.pc.job.JobList;
import warehouse.pc.job.LocationList;
import warehouse.shared.robot.Robot;

/**
 * The main interface for the whole project. Get the server using Server::get().
 * 
 * This holds the current state of the warehouse. The server's state can be updated by different
 * modules, and listeners will be notified of the updated state.
 * 
 * Later this will also hold the current jobs left to process, the jobs being processed, and the
 * jobs that have been completed.
 */
public class MainInterface {
	private volatile static Object interfaceInitLock = new Object();
	private volatile static MainInterface mainInterface = null;
	
	public static MainInterface get() {
		synchronized (interfaceInitLock) {
			if (mainInterface == null) {
				mainInterface = new MainInterface();
			}
			return mainInterface;
		}
	}
	
	private ArrayList<RobotListener> robotListeners;
	private State currentState;
	private BTServer server;
	
	private LocationList locList;
	private ItemList itemList;
	private JobList jobList;
	
	private MainInterface() {
		server = new BTServer();
		robotListeners = new ArrayList<>();
		currentState = new State(TestMaps.TEST_MAP4);
		
		locList = new LocationList("locations.csv");
		itemList = new ItemList("items.csv", locList);
		for (Item i : itemList.getList()) {
			System.out.println(i.getName() + ": reward:" + i.getReward()
			+ ", weight:" + i.getWeight() + ", [" + i.getX() + "," + i.getY() + "]");
		}
		jobList = new JobList("jobs.csv", itemList);
		Drop.setDropPoint("drops.csv");
	}
	
	/**
	 * Returns the job list. This contains the list of every job currently being tracked.
	 */
	public JobList getJobList() {
		return jobList;
	}
	
	/**
	 * Returns the item list that records what the reward and weight is for each item.
	 */
	public ItemList getItemList() {
		return itemList;
	}
	
	/**
	 * Returns the location list that records where the items are located in the map.
	 */
	public LocationList getLocationList() {
		return locList;
	}
	
	/**
	 * Gets the current bluetooth server that has been initialized.
	 */
	public BTServer getServer() {
		return server;
	}
	
	/**
	 * Adds a robot listener to the server that will be notified when a robot has been updated.
	 */
	public synchronized void addRobotListener(RobotListener _l) {
		robotListeners.add(_l);
	}
	
	/**
	 * Updated a robot {@code _r} with new information. If the robot is not recognized, a new robot is
	 * inserted into the array.
	 */
	public synchronized void updateRobot(Robot _r) {
		currentState.updateRobot(_r);
		for (RobotListener l : robotListeners) {
			l.robotChanged(_r);
		}
	}
	
	/**
	 * Gets the current state of the system.
	 */
	public synchronized State getCurrentState() {
		return currentState;
	}
	
	/**
	 * Perform cleanup operations and then call {@code System.exit(0)}
	 * e.g. telling all robots to shut down.
	 */
	public void close() {
		synchronized (interfaceInitLock) {
			synchronized (this) {
				mainInterface = null;
			}
		}
	}
}
