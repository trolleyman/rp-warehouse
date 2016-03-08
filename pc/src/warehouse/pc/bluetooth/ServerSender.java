package warehouse.pc.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The sender thread for the server. Sends messages to it's connected NXT.
 * 
 * @author Reece
 *
 */
public class ServerSender implements Runnable {

	private DataOutputStream toRobot;
	private LinkedBlockingQueue<String> toRobotQueue;
	private boolean running;

	/**
	 * Create the ServerSender.
	 * 
	 * @param toRobot The DataOutputStream to the NXT.
	 * @param toRobotQueue The queue of messages to send.
	 */
	public ServerSender(DataOutputStream toRobot, LinkedBlockingQueue<String> toRobotQueue) {
		this.toRobot = toRobot;
		this.toRobotQueue = toRobotQueue;
	}

	@Override
	public void run() {
		try {			
			
			System.out.println("Sender running");
			running = true;
			while (running) {
				String message = toRobotQueue.take();
				toRobot.writeUTF(message);
				toRobot.flush();
			}
			
		} catch (InterruptedException e) {
			System.err.println("ServerSender interupted, retrying take: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Sender error, disconnected NXT?");
		}
	}

}
