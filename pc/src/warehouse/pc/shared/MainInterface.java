package warehouse.pc.shared;

import java.util.ArrayList;

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
	private volatile static Object serverInitLock = new Object();
	private volatile static MainInterface server = null;
	
	public static MainInterface get() {
		synchronized (serverInitLock) {
			if (server == null) {
				server = new MainInterface();
			}
			return server;
		}
	}
	
	private ArrayList<RobotListener> robotListeners;
	private State currentState;
	
	private MainInterface() {
		robotListeners = new ArrayList<>();
		currentState = new State(TestMaps.TEST_MAP4, new Robot[0]);
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
		synchronized (serverInitLock) {
			synchronized (this) {
				server = null;
			}
		}
	}
}
