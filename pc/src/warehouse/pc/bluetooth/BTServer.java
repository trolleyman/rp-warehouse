package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import warehouse.pc.shared.MainInterface;
import warehouse.shared.robot.Robot;

/**
 * The BT communication "server". Can create new thread pairs for NXTs.
 * 
 * @author Reece
 *
 */
public class BTServer {

	public static int btProtocol;
	private NXTComm comm;

	// Maps of robot name against in/out queue
	HashMap<String, LinkedBlockingQueue<String>> toRobotQueues;
	HashMap<String, LinkedBlockingQueue<String>> fromRobotQueues;

	/**
	 * Setup the communication "server" for the current OS and driver. Initialise
	 * the maps of queues.
	 */
	public BTServer() {
		// Create the comms system for this OS and driver
		btProtocol = NXTCommFactory.BLUETOOTH;

		try {
			comm = NXTCommFactory.createNXTComm(btProtocol);
		} catch (NXTCommException e) {
			e.printStackTrace();
			System.err.println("Could not open the btCommunication");
		}

		toRobotQueues = new HashMap<>();
		fromRobotQueues = new HashMap<>();
	}

	/**
	 * Try to open a connection and threads to a NXT. First the in and output
	 * streams are made, then passed to the sender and receiver which are started
	 * in new threads.
	 * 
	 * @param nxt The protocol type, name and id of the NXT.
	 * @return True if the connection was opened and false if not.
	 */
	public boolean open(NXTInfo nxt) {
		try {

			System.out.println("Tring to open a connection");
			if (comm.open(nxt)) {
				System.out.println("Making stream and reader");
				DataOutputStream toRobot = new DataOutputStream(comm.getOutputStream());
				DataInputStream fromRobot = new DataInputStream(comm.getInputStream());

				System.out.println("Creating message queues");
				LinkedBlockingQueue<String> toRobotQueue = new LinkedBlockingQueue<>();
				LinkedBlockingQueue<String> fromRobotQueue = new LinkedBlockingQueue<>();
				toRobotQueues.put(nxt.name, toRobotQueue);
				fromRobotQueues.put(nxt.name, fromRobotQueue);

				System.out.println("Creating threads");
				Thread sender = new Thread(new ServerSender(toRobot, toRobotQueue));
				Thread receiver = new Thread(new ServerReceiver(fromRobot, fromRobotQueue));

				System.out.println("Starting threads");
				sender.start();
				receiver.start();
				
				MainInterface.get().updateRobot(new Robot(nxt.name, nxt.deviceAddress, 0, 0, 0.0));
				return true;
			}
		} catch (NXTCommException e) {
			System.err.println("Couldn't connect: " + e.getMessage());
		}

		// Connection did not open so return false;
		return false;
	}

	/**
	 * Send a string to a NXT.
	 * 
	 * @param robotName The name of the recipient robot.
	 * @param message The message string to send.
	 */
	public void sendToRobot(String robotName, String message) {
		toRobotQueues.get(robotName).offer(message);
	}

	// Should have a listener for incoming messages?
	// Maybe one for all robots and another for specific
}
