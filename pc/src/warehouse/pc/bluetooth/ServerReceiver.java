package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The receiver thread for the server. Receives messages from it's connected
 * NXT.
 * 
 * @author Reece
 *
 */
public class ServerReceiver implements Runnable {

	private boolean running;
	private DataInputStream fromRobot;
	private LinkedBlockingQueue<String> fromRobotQueue;
	private ArrayList<MessageListener> listeners;
	private String robotName;

	/**
	 * Create the receiver.
	 * 
	 * @param fromRobot The DataInputStream from the NXT.
	 * @param fromRobotQueue The queue to put received messages into.
	 */
	public ServerReceiver(DataInputStream fromRobot, LinkedBlockingQueue<String> fromRobotQueue, String robotName) {
		this.fromRobot = fromRobot;
		this.fromRobotQueue = fromRobotQueue;
		this.robotName = robotName;
		listeners = new ArrayList<>();
	}

	@Override
	public void run() {
		try {

			System.out.println("Receiver running");

			running = true;
			while (running) {
				System.out.println(robotName + " waiting to read");
				String input = fromRobot.readUTF();
				System.out.println("Got " + input + " from " + robotName);
				fromRobotQueue.put(input);
				notifyListeners(input);
			}

		} catch (IOException e) {
			System.err.println("An NXT disconnected: " + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Receiver was interupted for some reason");
		}
	}

	/**
	 * Add a listener to this robot.
	 * 
	 * @param listener The MessageListener to call when a new message is received.
	 */
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notify all listeners of a new message.
	 * 
	 * @param message The new message.
	 */
	private void notifyListeners(String message) {
		for (MessageListener l : listeners) {
			l.newMessage(robotName, message);
		}
	}
}
