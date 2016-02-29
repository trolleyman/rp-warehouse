package warehouse.pc.bluetooth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class RouteExecuter implements Runnable, MessageListener {

	private BTServer server;
	private HashMap<String, LinkedList<String>> commands;

	private boolean running;
	private int numRobots;
	private int finishedRobots;

	public RouteExecuter(BTServer server, HashMap<String, LinkedList<String>> commandMap) {
		this.server = server;
		this.commands = commandMap;
	}

	@Override
	public void run() {

	}

	@Override
	public void newMessage(String robotName, String message) {
		if (message.equals("ready")) {
			finishedRobots++;
		}

		if (finishedRobots == numRobots) {
			sendNextMove();
		}
	}

	private void sendNextMove() {
		for (Entry<String, LinkedList<String>> e : commands.entrySet()) {
			server.sendToRobot(e.getKey(), e.getValue().pop());
		}
		
		finishedRobots = 0;
	}

	public void setNumRobots(int numRobots) {
		this.numRobots = numRobots;
	}
	
	public void changeNumRobots(int change) {
		this.numRobots += change;
	}

	public int getNumRobots() {
		return numRobots;
	}
}
