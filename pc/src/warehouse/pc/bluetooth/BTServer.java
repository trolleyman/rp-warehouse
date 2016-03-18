package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Robot;

/**
 * The BT communication "server". Can create new thread pairs for NXTs.
 * 
 * @author Reece
 *
 */
public class BTServer {

	public static int btProtocol;

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
	 * Closes a connection with a robot
	 */
	public synchronized void close(NXTInfo nxt) {
		Connection con = connections.get(nxt.name);
		if (con != null)
			con.close();
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
			return false;
		}
		
		Connection connection = new Connection(nxt, fromRobot, toRobot);

		// Create the connection
		connections.put(nxt.name, connection);
		try {
			sendToRobot(nxt.name, Format.robot(nxt.name, 0, 0, ""));
			waitForReady(nxt.name);
		} catch (IOException e) {
			return false;
		}

		// Update the robot in the MainInterface
		// MainInterface.get().updateRobot(new Robot(nxt.name, nxt.deviceAddress, 0,
		// 0, 0.0));

		System.out.println("Connection made to " + name);
		return true;
	}
	
	public void sendCommand(Robot robot, Command com) throws IOException {
		// TODO: com.getX().get()
		switch (com) {
		case LEFT:
			sendToRobot(robot.getName(), Format.goLeft(0, 0));
			break;
		case RIGHT:
			sendToRobot(robot.getName(), Format.goRight(0, 0));
			break;
		case FORWARD:
			sendToRobot(robot.getName(), Format.goForward(0, 0));
			break;
		case BACKWARD:
			sendToRobot(robot.getName(), Format.goBackward(0, 0));
			break;
		case PICK:
			sendToRobot(robot.getName(), Format.pickUp(com.getQuantity().get(), com.getWeight().get()));
			break;
		case DROP:
			sendToRobot(robot.getName(), Format.dropOff());
			break;
		case WAIT:
			break;
		}
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
	 * @throws IOException
	 */
	public void sendToRobot(String robotName, String message) throws IOException {
		connections.get(robotName).send(message);
	}
	
	public void waitForReady(String robotName) throws IOException {
		String msg = null;
		while (msg == null || !msg.equalsIgnoreCase("ready")) {
			msg = listen(robotName);
			System.out.println("Recv from " + robotName + ": Wanted 'ready', got: " + msg);
		}
	}
	
	public void waitForReady(Robot robot) throws IOException {
		String msg = null;
		while (msg == null || !msg.equalsIgnoreCase("ready")) {
			msg = listen(robot.getName());
			String dist = "Distance:";
			if (msg.startsWith(dist)) {
				String distString = msg.substring(dist.length(), msg.length() - 1);
				try {
					int d = Integer.parseInt(distString);
					MainInterface.get().distanceRecieved(robot, d);
					// Ignore message: don't print out debug message
					continue;
				} catch (NumberFormatException e) {
					// Ignore.
				}
			}
			System.out.println("Recv from " + robot.getName() + ": Wanted 'ready', got: " + msg);
		}
	}
	
	public String listen(String robotName) throws IOException {
		return connections.get(robotName).listen();
	}

	public static Object getLock() {
		return btLock;
	}
}
