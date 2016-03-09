package warehouse.pc.bluetooth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class RouteExecuter implements MessageListener {

	private BTServer server;
	private HashMap<String, LinkedList<String>> commands;

	private int numRobots;
	private int finishedRobots;

	public RouteExecuter(BTServer server, HashMap<String, LinkedList<String>> commandMap) {
		this.server = server;
		this.commands = commandMap;
	}

	@Override
	public void newMessage(String robotName, String message) {
		if (message.equals("ready")) {
			finishedRobots++;
			System.out.println("Num: " + numRobots + " Ready: " + finishedRobots);
		}

		if (finishedRobots == numRobots) {
			System.out.println("All robots ready");
			sendNextMove();
		}
	}

	private void sendNextMove() {
		for (Entry<String, LinkedList<String>> e : commands.entrySet()) {
			String next = e.getValue().peek();
			System.out.println(e.getKey() + ": " + next);
			if (next != null) {
				server.sendToRobot(e.getKey(), next);
				e.getValue().removeFirst();
			} else {
				System.out.println("Reached end of the list for " + e.getKey());
				changeNumRobots(-1);
				commands.remove(e.getKey());
			}
		}
		
		finishedRobots = 0;
	}

	public void setNumRobots(int numRobots) {
		this.numRobots = numRobots;
		System.out.println("Num robots now " + numRobots);
	}
	
	public void changeNumRobots(int change) {
		this.numRobots += change;
		System.out.println("Num robots now " + numRobots);
	}

	public int getNumRobots() {
		return numRobots;
	}

	public int getFinishedRobots() {
		return finishedRobots;
	}
}
