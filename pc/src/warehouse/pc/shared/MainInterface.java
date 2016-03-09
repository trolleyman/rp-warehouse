package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.HashSet;

import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.job.DropList;
import warehouse.pc.job.ItemList;
import warehouse.pc.job.JobList;
import warehouse.pc.job.LocationList;
import warehouse.pc.shared.Robot;

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
	private ArrayList<DistanceListener> distanceListeners;
	
	private State currentState;
	private BTServer server;
	
	private RobotManager robotManager;
	
	private LocationList locList;
	private ItemList itemList;
	private JobList jobList;
	private DropList dropList;
	
	private MainInterface() {
		server = new BTServer();
		
		robotManager = null; // For now
		
		robotListeners = new ArrayList<>();
		distanceListeners = new ArrayList<>();
		
		// currentState = new State(new Map(new GridMap(10, 7, 14, 31, 30, MapUtils.create2014Map2())));
		// currentState = new State(new Map(MapUtils.createRealWarehouse()));
		currentState = new State(new Map(MapUtils.createRealWarehouse()));
		
		locList = new LocationList("locations.csv");
		itemList = new ItemList("items.csv", locList);
//		for (Item i : itemList.getList()) {
//			System.out.println(i.getName() + ": reward:" + i.getReward()
//			+ ", weight:" + i.getWeight() + ", [" + i.getX() + "," + i.getY() + "]");
//		}
		jobList = new JobList("jobs.csv", itemList);
		dropList = new DropList("drops.csv");
	}
	
	/**
	 * Returns the robot manager that is in control of all the robots.
	 */
	public RobotManager getRobotManager() {
		throw new AssertionError("RobotManager unimplemented.");
	}
	
	/**
	 * Returns the drop list. This contains the list of every drop location.
	 */
	public DropList getDropList() {
		return dropList;
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
	 * Adds a robot distance listener to the program that will be notified whenever a distance is recieved.
	 */
	public synchronized void addDistanceListener(DistanceListener _l) {
		distanceListeners.add(_l);
	}
	
	/**
	 * Notifies all distance listeners that a distance has been recieved from a robot.
	 */
	public synchronized void distanceRecieved(Robot _robot, int _dist) {
		for (DistanceListener l : distanceListeners) {
			l.distanceRecieved(_robot, _dist);
		}
	}
	
	/**
	 * Adds a robot listener to the server that will be notified when a robot has been updated.
	 */
	public synchronized void addRobotListener(RobotListener _l) {
		robotListeners.add(_l);
	}
	
	/**
	 * Gets the current map
	 */
	public synchronized Map getMap() {
		return currentState.getMap();
	}
	
	/**
	 * Gets all the current robots and their statuses.
	 * ***Don't modify this directly*** - Use MainInterface.updateRobot / MainInterface.removeRobot.
	 */
	public synchronized HashSet<Robot> getRobots() {
		return currentState.getRobots();
	}
	
	/**
	 * Updated a robot {@code _r} with new information. If the robot is not recognized, a new robot is
	 * inserted into the array.
	 */
	public synchronized void updateRobot(Robot _r) {
		boolean added = false;
		if (currentState.getRobots().contains(_r)) {
			added = true;
		}
		if (added) {
			for (RobotListener l : robotListeners) {
				l.robotAdded(_r);
			}
		} else {
			for (RobotListener l : robotListeners) {
				l.robotChanged(_r);
			}
		}
	}
	
	/**
	 * Removes a robot if it exists
	 * @param _r the robot
	 */
	public synchronized void removeRobot(Robot _r) {
		if (getRobots().contains(_r)) {
			currentState.removeRobot(_r);
			for (RobotListener l : robotListeners) {
				l.robotRemoved(_r);
			}
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
				System.exit(0);
			}
		}
	}
}
