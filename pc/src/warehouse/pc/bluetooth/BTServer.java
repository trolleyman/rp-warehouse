package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * The BT communication "server". Can create new thread pairs for NXTs.
 * 
 * @author Reece
 *
 */
public class BTServer {

	public static int btProtocol;

	private static final long TIMEOUT_MILLIS = 5000;
	private Boolean openSuccess = false;

	// The RouteExecuter and a HashMap of robot names to list of commands
	private RouteExecuter executer;
	private HashMap<String, LinkedList<String>> commandMap;
	
	// HashMap of robot names to connection class
	private HashMap<String, Connection> connections;
	
	// The lock for Bluetooth communications
	private final static Object btLock = new Object();

	/**
	 * Setup the communication "server" for the current OS and driver. Initialise
	 * the maps of queues.
	 */
	public BTServer() {
		btProtocol = NXTCommFactory.BLUETOOTH;

		commandMap = new HashMap<>();
		executer = new RouteExecuter(this, commandMap);
		new Thread(executer, "RouteExecuter").start();
		connections = new HashMap<>();
	}

	/**
	 * Try to open a connection and threads to a NXT. First the in and output
	 * streams are made, then passed to the sender and receiver which are started
	 * in new threads.
	 * 
	 * Times out after TIMEOUT_MILIS
	 * 
	 * @param nxt The protocol type, name and id of the NXT.
	 * @return True if the connection was opened and False if not.
	 */
	public synchronized boolean open(NXTInfo nxt) {
		String name = nxt.name + " (" + nxt.deviceAddress + ")";
		System.out.println("Trying connect to " + name + ".");
		
		DataOutputStream toRobot = null;
		DataInputStream fromRobot = null;
		
		NXTComm comm = null;
		
		try {
			comm = NXTCommFactory.createNXTComm(btProtocol);
			if (comm.open(nxt)) {
				toRobot = new DataOutputStream(comm.getOutputStream());
				fromRobot = new DataInputStream(comm.getInputStream());
			}
		} catch (NXTCommException e) {
			e.printStackTrace();
		}
		
		Connection connection = new Connection(nxt, fromRobot, toRobot);

		// Create the connection
		connections.put(nxt.name, connection);

		// Update the robot in the MainInterface
		// MainInterface.get().updateRobot(new Robot(nxt.name, nxt.deviceAddress, 0,
		// 0, 0.0));

		System.out.println("Connection made to " + name);
		return true;
	}

	/**
	 * Set the list of commands which will be send to a specific NXT. If there is
	 * already a list for the NXT with this name then it will be overwritten.
	 * 
	 * @param robotName The name of the robot the commands are for.
	 * @param commands The LinkedList of String commands.
	 */
	public void sendCommands(String robotName, LinkedList<String> commands) {
		System.out.println("Send commands for " + robotName);
		commandMap.put(robotName, commands);
	}

	/**
	 * Send a string to a NXT.
	 * 
	 * This is used internally by the server and should not be used to send
	 * messages by classes other than server classes!
	 * 
	 * @param robotName The name of the recipient robot.
	 * @param message The message string to send.
	 */
	public void sendToRobot(String robotName, String message) {
		connections.get(robotName).send(message);
	}
	
	public String listen(String robotName) {
		return connections.get(robotName).listen();
	}

	public static Object getLock() {
		return btLock;
	}
}
