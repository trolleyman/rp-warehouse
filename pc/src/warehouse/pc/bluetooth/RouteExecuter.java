package warehouse.pc.bluetooth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class RouteExecuter implements Runnable {

	private BTServer server;
	private HashMap<String, LinkedList<String>> commands;
	private boolean running;

	public RouteExecuter(BTServer server, HashMap<String, LinkedList<String>> commandMap) {
		this.server = server;
		this.commands = commandMap;
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			
			// If there are no commands to send, sleep for a second
			if (commands.isEmpty()) {
				try {
					//System.out.println("No commands for any robots, sleeping for 1000ms");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			for (Entry<String, LinkedList<String>> entry : commands.entrySet()) {
				String robotName = entry.getKey();
				if (!entry.getValue().isEmpty()) {
					String command = entry.getValue().pop();
					server.sendToRobot(robotName, command);
				} else {
					commands.remove(robotName);
					System.out.println("End of list for " + robotName);
					server.sendToRobot(robotName, "end");
				}
			}

			for (Entry<String, LinkedList<String>> entry : commands.entrySet()) {
				String robotName = entry.getKey();
				System.out.println("Waiting for reply from " + robotName);
				String reply = server.listen(robotName);
				if (!reply.equals("ready")) {
					System.err.println("Robot not ready.");
				}
			}
		}
	}
}
