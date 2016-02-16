package warehouse.shared;

import java.util.ArrayList;

import warehouse.gui.State;
import warehouse.gui.TestStates;

/**
 * The main interface for the whole project. Get the server using Server::get().
 * 
 * This holds the current state of the warehouse. The server's state can be updated by different
 * modules, and listeners will be notified of the updated state.
 */
public class Server {
	private static Object serverInitLock = new Object();
	private static Server server = null;
	
	public static Server get() {
		synchronized (serverInitLock) {
			if (server == null) {
				server = new Server();
			}
			return server;
		}
	}
	
	private ArrayList<RobotListener> robotListeners;
	private State currentState;
	
	private Server() {
		robotListeners = new ArrayList<>();
		currentState = TestStates.TEST_STATE1;
	}
	
	/**
	 * Adds a robot listener to the server that will be notified when a robot has been updated.
	 */
	public synchronized void addRobotListener(RobotListener _l) {
		robotListeners.add(_l);
	}
	
	/**
	 * Updated a robot {@code _r} with new information.
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
