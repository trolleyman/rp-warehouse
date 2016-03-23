package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.shared.Direction;

/**
 * A new and (hopefully) improved version of the multi robot route finding T
 * time to fly my friends
 * 
 * @author George Kaye
 *
 */

public class NewMultiRoutePlanner {

	private Map map;
	private int timeWindow;
	private float maxWeight;

	private RouteFinder oneFinder;
	private MultiRouteFinder finder;

	private ArrayList<Junction> bases;

	private HashMap<Robot, LinkedList<Job>> pairedJobs;
	private HashMap<Robot, CommandQueue> pairedCommands;
	private HashMap<Junction, Command> escapeMap;

	private HashMap<Robot, Boolean> idle;
	private HashMap<Robot, ArrayList<ItemQuantity>> itemq;
	private HashMap<Junction, Robot> waitMap;

	// for debugging

	private Robot robot1;
	private Robot robot2;
	private Robot robot3;

	/**
	 * Create a new multi robot route planner
	 * 
	 * @param _map
	 *            the map
	 * @param _maxWeight
	 *            the max weight
	 * @param _jobs
	 *            the hashmap of robots to lists of jobs
	 * @param _bases
	 *            the list of bases
	 * @param _timeWindow
	 *            the time window
	 * @param _escapeMap
	 *            the hashmap of junctions to where the robot can escape
	 */

	public NewMultiRoutePlanner(Map _map, float _maxWeight, HashMap<Robot, LinkedList<Job>> _jobs,
			ArrayList<Junction> _bases, int _timeWindow) {

		this.map = _map;
		this.maxWeight = _maxWeight;
		this.pairedJobs = _jobs;
		this.bases = _bases;
		this.timeWindow = _timeWindow;

		robot1 = getRobot(pairedJobs, 0);
		robot2 = getRobot(pairedJobs, 1);
		robot3 = getRobot(pairedJobs, 2);

		oneFinder = new RouteFinder(map);
		finder = new MultiRouteFinder(map);

		pairedCommands = new HashMap<>();
		idle = new HashMap<>();
		itemq = new HashMap<>();
		waitMap = new HashMap<>();

		setUp();

	}

	/**
	 * Sets up the various hashmaps and makes sure it doesn't break It probably
	 * will break though
	 */

	private void setUp() {

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			// filling the various hashmaps

			idle.put(entry.getKey(), false);
			pairedCommands.put(entry.getKey(), new CommandQueue());
			itemq.put(entry.getKey(), null);

			for (Job job : entry.getValue()) {

				// checking that each job can be carried by the robot

				if (job.getTotalWeight() > maxWeight) {
					System.out.println("WARNING: too much weight in job " + job);
					System.out.println("removing job");
					entry.getValue().removeFirstOccurrence(job);
				}

			}

		}
	}

	/**
	 * Gets the command queue for a given robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the command queue
	 */

	public CommandQueue getCommands(Robot _robot) {
		return pairedCommands.get(_robot);
	}

	/**
	 * Gets the hashmap of robots mapped to command queues for a given robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the hashmap
	 */

	public HashMap<Robot, CommandQueue> getPairedCommands(Robot _robot) {
		return this.pairedCommands;
	}

	/**
	 * Update the route planner with a new hashmap of robots to linked list of
	 * jobs
	 * 
	 * @param _hashBrown
	 *            the new hashmap
	 */

	public void update(HashMap<Robot, LinkedList<Job>> _hashBrown) {

		this.pairedJobs = _hashBrown;

		setUp();
	}

	/**
	 * Get the junction for an item
	 * 
	 * @param item
	 *            the item
	 * @return the junction
	 */

	public Junction getJunction(Item item) {
		return map.getJunction((int) item.getX(), (int) item.getY());
	}

	/**
	 * Get the junction for a robot
	 * 
	 * @param robot
	 *            the robot
	 * @return the junction
	 */

	public Junction getJunction(Robot robot) {
		return map.getJunction((int) robot.getX(), (int) robot.getY());
	}

	/***
	 * Helper method to get a robot at a given index of a hashmap of robots to
	 * jobs
	 * 
	 * @param hashMap
	 *            the hashmap
	 * @param x
	 *            the index
	 * @return the robot
	 */

	private Robot getRobot(HashMap<Robot, LinkedList<Job>> hashMap, int x) {

		int i = 0;

		for (Entry<Robot, LinkedList<Job>> entry : hashMap.entrySet()) {
			if (i == x) {
				return entry.getKey();
			}
			i++;
		}

		return null;
	}

	/**
	 * Compute the commands for multiple robots: these commands will be added to
	 * pairedCommands which can be used by other classes
	 */

	public void computeCommands() {

		// the ghetto arraylist returns

		@SuppressWarnings("unchecked")
		ArrayList<Junction>[] reserveTable = (ArrayList<Junction>[]) new ArrayList<?>[timeWindow];

		for (int i = 0; i < reserveTable.length; i++) {
			reserveTable[i] = new ArrayList<Junction>();
		}

		// set up the initial robot priorities
		// these will change but will be used a pointers for the hashmap

		Robot priority1 = getRobot(pairedJobs, 0);
		Robot priority2 = getRobot(pairedJobs, 1);
		Robot priority3 = getRobot(pairedJobs, 2);

		// set up some empty job objects, these will be used later

		Job jobA = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);
		Job jobB = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);
		Job jobC = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);

		// each robot has a number of jobs, some robots might have more than
		// others
		// to stop it breaking, we have to find the longest job list
		// this is the number we will iterate until
		// hopefully job selection (easiest job) will have made it fairly equal

		double longestJobList = Math.max(pairedJobs.get(priority1).size(),
				Math.max(pairedJobs.get(priority2).size(), pairedJobs.get(priority3).size()));

		for (int i = 0; i < longestJobList * 2; i++) {

			if (i % 2 == 0) {

				idle.put(priority1, false);
				idle.put(priority2, false);
				idle.put(priority3, false);

				// assign a job to each robot
				// since we're using priorities we need to get it from the
				// hashmap
				// if that robot has run out of jobs set it as idle

				if (pairedJobs.get(priority1).size() > i / 2) {
					jobA = pairedJobs.get(priority1).get(i / 2);
				} else {
					idle.put(priority1, true);
				}

				if (pairedJobs.get(priority2).size() > i / 2) {
					jobB = pairedJobs.get(priority2).get(i / 2);
				} else {
					idle.put(priority2, true);
				}

				if (pairedJobs.get(priority1).size() > i / 2) {
					jobC = pairedJobs.get(priority3).get(i / 2);
				} else {
					idle.put(priority1, true);
				}

				// the jobs will all be different sizes (but hopefully similar
				// sizes)
				// first we need to get the list of items for this iteration

				itemq.put(priority1, jobA.getItems());
				itemq.put(priority2, jobB.getItems());
				itemq.put(priority3, jobC.getItems());

				// like before we will iterate for the longest list size

				double longestItemSize = Math.max(itemq.get(priority1).size(),
						Math.max(itemq.get(priority2).size(), itemq.get(priority3).size()));

				for (int j = 0; j < longestItemSize; j++) {

					Item itemA = new Item(null, 0, 0, 0, 0);
					Item itemB = new Item(null, 0, 0, 0, 0);
					Item itemC = new Item(null, 0, 0, 0, 0);

					// now each robot will get an item
					// if there are no items left it will be set as idle

					if (itemq.get(priority1).size() > j) {
						itemA = itemq.get(priority1).get(j).getItem();
					} else {
						idle.put(priority1, true);
						waitMap.put(getJunction(priority2), priority2);
					}

					if (itemq.get(priority2).size() > j) {
						itemB = itemq.get(priority2).get(j).getItem();
					} else {
						idle.put(priority2, true);
						waitMap.put(getJunction(priority2), priority2);
					}

					if (itemq.get(priority3).size() > j) {
						itemC = itemq.get(priority3).get(j).getItem();
					} else {
						idle.put(priority3, true);
						waitMap.put(getJunction(priority3), priority3);
					}

					// as long as one robot is not idle, we will keep finding
					// routes

					while (!idle.get(priority1) || !idle.get(priority2) || !idle.get(priority3)) {

						if (!idle.get(priority1)) {
							Junction end = findTheRoute(getJunction(priority1),
									getJunction(itemq.get(priority1).get(j).getItem()), reserveTable, priority1);
							if (end.equals(getJunction(itemq.get(priority1).get(j).getItem()))) {
								pairedCommands.get(priority1).addCommand(Command.PICK);
								idle.put(priority1, true);
								waitMap.put(end, priority1);
							}
						} else {
							for (int l = 0; l < timeWindow; l++) {
								reserveTable[l].add(getJunction(priority1));
							}
						}

						if (!idle.get(priority2)) {
							Junction end = findTheRoute(getJunction(priority2),
									getJunction(itemq.get(priority2).get(j).getItem()), reserveTable, priority2);
							if (end.equals(getJunction(itemq.get(priority2).get(j).getItem()))) {
								pairedCommands.get(priority2).addCommand(Command.PICK);
								idle.put(priority2, true);
								waitMap.put(end, priority2);
							}
						} else {
							for (int l = 0; l < timeWindow; l++) {
								reserveTable[l].add(getJunction(priority2));
							}
						}

						if (!idle.get(priority3)) {
							Junction end = findTheRoute(getJunction(priority3),
									getJunction(itemq.get(priority3).get(j).getItem()), reserveTable, priority3);
							if (end.equals(getJunction(itemq.get(priority3).get(j).getItem()))) {
								pairedCommands.get(priority3).addCommand(Command.PICK);
								idle.put(priority3, true);
								waitMap.put(end, priority3);
							}
						} else {
							for (int l = 0; l < timeWindow; l++) {
								reserveTable[l].add(getJunction(priority3));
							}
						}

						for (int l = 0; l < timeWindow; l++) {
							reserveTable[l] = new ArrayList<>();
						}

						Robot priorityTemp = priority1;
						priority1 = priority2;
						priority2 = priority3;
						priority3 = priorityTemp;

					}

					System.out.println(priority1.getName() + " (item): " + pairedCommands.get(priority1).getCommands());
					System.out.println(priority2.getName() + " (item): " + pairedCommands.get(priority2).getCommands());
					System.out.println(priority3.getName() + " (item): " + pairedCommands.get(priority3).getCommands());

					idle.put(priority1, false);
					idle.put(priority2, false);
					idle.put(priority3, false);

					waitMap.clear();

				}

			} else {

				HashMap<Robot, Junction> basem = new HashMap<>();

				basem.put(priority1, findBase(getJunction(priority1), priority1));
				basem.put(priority2, findBase(getJunction(priority2), priority2));
				basem.put(priority3, findBase(getJunction(priority3), priority3));

				int rekt = 0;
				
				while (!idle.get(priority1) || !idle.get(priority2) || !idle.get(priority3)) {
					
					if (!idle.get(priority1)) {
						Junction end = findTheRoute(getJunction(priority1), basem.get(priority1), reserveTable,
								priority1);
						if (end.equals(basem.get(priority1))) {
							pairedCommands.get(priority1).addCommand(Command.DROP);
							idle.put(priority1, true);
							waitMap.put(end, priority1);
						}
					} else {
						for (int l = 0; l < timeWindow; l++) {
							reserveTable[l].add(getJunction(priority1));
						}
					}

					if (!idle.get(priority2)) {
						Junction end = findTheRoute(getJunction(priority2), basem.get(priority2), reserveTable,
								priority2);
						if (end.equals(basem.get(priority2))) {
							pairedCommands.get(priority2).addCommand(Command.DROP);
							idle.put(priority2, true);
							waitMap.put(end, priority2);
						}
					} else {
						for (int l = 0; l < timeWindow; l++) {
							reserveTable[l].add(getJunction(priority2));
						}
					}

					if (!idle.get(priority3)) {
						Junction end = findTheRoute(getJunction(priority1), basem.get(priority3), reserveTable,
								priority3);
						if (end.equals(basem.get(priority3))) {
							pairedCommands.get(priority3).addCommand(Command.DROP);
							idle.put(priority3, true);
							waitMap.put(end, priority3);
						}
					} else {
						for (int l = 0; l < timeWindow; l++) {
							reserveTable[l].add(getJunction(priority1));
						}
					}

					for (int l = 0; l < timeWindow; l++) {
						reserveTable[l] = new ArrayList<>();
					}

					Robot priorityTemp = priority1;
					priority1 = priority2;
					priority2 = priority3;
					priority3 = priorityTemp;
					
					rekt++;

				}

				System.out.println(priority1.getName() + " (base) : " + pairedCommands.get(priority1).getCommands());
				System.out.println(priority2.getName() + " (base) : " + pairedCommands.get(priority2).getCommands());
				System.out.println(priority3.getName() + " (base) : " + pairedCommands.get(priority3).getCommands());

				idle.put(priority1, false);
				idle.put(priority2, false);
				idle.put(priority3, false);

				waitMap.clear();

			}

		}
	}

	/**
	 * Helper method to find a route from one junction to another
	 * 
	 * @param start
	 *            the start point
	 * @param goal
	 *            the end point
	 * @param reserveTable
	 *            the reserve table
	 * @param robot
	 *            the robot
	 * @return
	 */

	private Junction findTheRoute(Junction start, Junction goal, ArrayList<Junction>[] reserveTable, Robot robot) {

		System.out.println(robot.getName() + " " + start + " to " + goal);

		if (waitMap.containsKey(goal)) {

			Junction newJ = escape(waitMap.get(goal), goal);

			for (int l = 0; l < timeWindow; l++) {
				reserveTable[l].add(newJ);
			}

		}

		RoutePackage rPackage = finder.findRoute(start, goal, robot.getDirection(), reserveTable);

		ArrayList<Direction> directionList = rPackage.getDirectionList();
		ArrayList<Junction> junctionList = rPackage.getJunctionList();
		LinkedList<Command> commandList = rPackage.getCommandList();

		Junction endPoint = junctionList.get(junctionList.size() - 1);
		Direction endDirection = directionList.get(directionList.size() - 1);

		if (waitMap.containsKey(endPoint)) {
			Command last = commandList.removeLast();
			commandList.add(Command.WAIT);
			commandList.add(last);
		}

		robot.setX(endPoint.getX());
		robot.setY(endPoint.getY());
		robot.setDirection(endDirection);

		pairedCommands.get(robot).addCommandList(commandList);

		if (junctionList.size() < timeWindow) {
			for (int l = junctionList.size(); l < timeWindow; l++) {
				reserveTable[l].add(getJunction(robot));
			}
		}

		return endPoint;

	}

	/**
	 * Helper method to force a waiting robot to move if another robot is on its
	 * way
	 * 
	 * @param robot
	 *            the waiting robot
	 * @param junction
	 *            where it is waiting
	 */

	private Junction escape(Robot robot, Junction junction) {

		for (Junction junc : junction.getNeighbours()) {

			for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

				Junction a = getJunction(entry.getKey());

				if (!a.equals(junc)) {

					Direction facing = robot.getDirection();

					RoutePackage rPack = findTheWay(facing, junction, junc);

					pairedCommands.get(robot).addCommand(rPack.getCommandList().pop());
					pairedCommands.get(robot).addCommand(Command.ESC);

					waitMap.put(junc, robot);
					waitMap.remove(junction);
					
					robot.setX(junc.getX());
					robot.setY(junc.getY());
					robot.setDirection(rPack.getDirectionList().get(0));

					return junc;

				}

			}

		}

		return null;
		
	}

	/**
	 * Helper method to find the command to give to a robot in order to move
	 * between two spaces
	 * 
	 * @param direction
	 *            the initial direction of the robot
	 * @param first
	 *            the first junction
	 * @param second
	 *            the second junction
	 * @return the command
	 */

	private RoutePackage findTheWay(Direction direction, Junction first, Junction second) {

		Direction way = null;

		if (second.getX() > first.getX()) {
			way = Direction.X_POS;
		}

		if (second.getX() < first.getX()) {
			way = Direction.X_NEG;
		}

		if (second.getY() > first.getY()) {
			way = Direction.Y_POS;
		}

		if (second.getY() < first.getY()) {
			way = Direction.Y_NEG;
		}

		Command command = null;

		switch (direction) {
		case Y_POS:
			switch (way) {
			case Y_POS:
				command = Command.FORWARD;
				break;
			case Y_NEG:
				command = Command.BACKWARD;
				break;
			case X_POS:
				command = Command.RIGHT;
				break;
			case X_NEG:
				command = Command.LEFT;
				break;

			}
			break;

		case Y_NEG:
			switch (way) {
			case Y_NEG:
				command = Command.FORWARD;
				break;
			case Y_POS:
				command = Command.BACKWARD;
				break;
			case X_NEG:
				command = Command.RIGHT;
				break;
			case X_POS:
				command = Command.LEFT;
				break;
			}
			break;

		case X_POS:
			switch (way) {
			case X_POS:
				command = Command.FORWARD;
				break;
			case X_NEG:
				command = Command.BACKWARD;
				break;
			case Y_NEG:
				command = Command.RIGHT;
				break;
			case Y_POS:
				command = Command.LEFT;
				break;

			}
			break;

		case X_NEG:
			switch (way) {
			case X_NEG:
				command = Command.FORWARD;
				break;
			case X_POS:
				command = Command.BACKWARD;
				break;
			case Y_POS:
				command = Command.RIGHT;
				break;
			case Y_NEG:
				command = Command.LEFT;
				break;

			}

			break;
		}

		ArrayList<Direction> dList = new ArrayList<>();
		dList.add(way);

		LinkedList<Command> cList = new LinkedList<>();
		cList.add(command);

		ArrayList<Junction> jList = new ArrayList<>();
		jList.add(first);
		jList.add(second);

		return new RoutePackage(dList, cList, jList);
	}

	/**
	 * Helper method to return the closest base
	 * 
	 * @param start
	 *            the start position
	 * @param robot
	 *            the robot
	 * @return the closest base
	 */

	private Junction findBase(Junction start, Robot robot) {

		Junction closest = new Junction(0, 0);
		int lowest = map.getHeight() + map.getWidth();

		for (Junction base : bases) {
			int heury = oneFinder
					.findRoute(map.getJunction((int) start.getX(), (int) start.getY()), base, robot.getDirection())
					.getDirectionList().size();
			if (heury < lowest) {
				closest = base;
				lowest = heury;
			}
		}

		return closest;
	}

}
