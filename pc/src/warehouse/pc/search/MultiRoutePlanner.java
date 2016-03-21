package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sun.jndi.rmi.registry.ReferenceWrapper;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;

/**
 * Class to create lists of bearings for individual robots to take Uses a lot of
 * placeholders while the job and robot teams get their classes together
 *
 */

public class MultiRoutePlanner {

	private HashMap<Robot, LinkedList<Job>> pairedJobs;
	private HashMap<Robot, CommandQueue> pairedCommands;
	private Robot robot1;
	private Robot robot2;
	private Robot robot3;
	private MultiRouteFinder finder;
	private Map map;
	private Float maxWeight;
	private HashMap<Robot, Float> weights;
	private ArrayList<Junction> bases;
	private int timeWindow;
	private HashMap<Robot, ArrayList<ItemCollection>> weightedJobs;
	private HashMap<Junction, Robot> baseStatus;
	private boolean idle1;
	private boolean idle2;
	private boolean idle3;
	private boolean base1;
	private boolean base2;
	private boolean base3;

	/**
	 * Create a new RoutePlanner for a map
	 * 
	 * @param _map
	 *            the map
	 * @param _maxWeight
	 *            the maximum weight of the robot
	 * @param _jobs
	 *            the hashmap of robots to jobs
	 * @param _commands
	 *            the hashmap of robots to commands
	 * @param _base
	 *            the dropoff point
	 */

	public MultiRoutePlanner(Map _map, Float _maxWeight, HashMap<Robot, LinkedList<Job>> _jobs,
			ArrayList<Junction> _dropList, int _timeWindow) {

		finder = new MultiRouteFinder(_map);
		maxWeight = _maxWeight;
		pairedJobs = _jobs;
		map = _map;
		timeWindow = _timeWindow;

		idle1 = false;
		idle2 = false;
		idle3 = false;

		base1 = false;
		base2 = false;
		base3 = false;

		pairedCommands = new HashMap<Robot, CommandQueue>();
		weightedJobs = new HashMap<Robot, ArrayList<ItemCollection>>();

		bases = _dropList;

		baseStatus = new HashMap<Junction, Robot>();

		for (Junction base : bases) {
			baseStatus.put(base, null);
		}

		weights = new HashMap<Robot, Float>();

		setUpJobs();

	}

	/**
	 * Helper method to set up robots on initialisation
	 * 
	 * @param robot
	 *            the robot
	 * @param i
	 *            the number
	 */

	private void setUpRobots(Robot robot, int i) {

		switch (i) {
		case 0:
			robot1 = robot;
			break;
		case 1:
			robot2 = robot;
			break;
		case 2:
			robot3 = robot;
			break;
		default:
			break;
		}

	}

	/**
	 * Split the jobs into collections the robot can manage at once
	 */

	private void setUpJobs() {

		// make a command queue for every robot, and put them in a hash table
		int i = 0;

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			setUpRobots(entry.getKey(), i);
			i++;
			pairedCommands.put(entry.getKey(), new CommandQueue());
			weights.put(entry.getKey(), 0f);
			ItemCollection itemColl = new ItemCollection();
			weightedJobs.put(entry.getKey(), new ArrayList<ItemCollection>());
			weightedJobs.get(entry.getKey()).add(itemColl);

			for (Job job : entry.getValue()) {

				ArrayList<ItemQuantity> tempItems = job.getItems();

				for (ItemQuantity item : tempItems) {

					float totalWeight = item.getItem().getWeight() * item.getQuantity();

					if ((totalWeight + weights.get(entry.getKey())) <= maxWeight) {
						weights.put(entry.getKey(), weights.get(entry.getKey()) + totalWeight);
						itemColl.addItem(item);

					} else {

						itemColl = new ItemCollection();
						itemColl.addItem(item);
						weightedJobs.get(entry.getKey()).add(itemColl);
						weights.put(entry.getKey(), totalWeight);
					}
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
	 * Get the next command for a robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the next command
	 */

	public Command getNextCommand(Robot _robot) {
		return pairedCommands.get(_robot).getNextCommand();
	}

	/**
	 * Gets the hashmap of robots mapped to commands for a given robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the hashmap
	 */

	public HashMap<Robot, CommandQueue> getPairedCommands(Robot _robot) {
		return this.pairedCommands;
	}

	/**
	 * Update the hashmaps with a new set of robots and linked lists
	 * 
	 * @param _hash
	 *            the new hashmap
	 */

	public void update(HashMap<Robot, LinkedList<Job>> _hash) {

		this.pairedJobs = _hash;

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			pairedCommands.put(entry.getKey(), new CommandQueue());
		}

		for (Entry<Robot, CommandQueue> entry : pairedCommands.entrySet()) {

			weights.put(entry.getKey(), 0f);

		}

		setUpJobs();
	}

	private void setState(Robot robot, boolean idle) {

		if (robot.equals(robot1)) {
			idle1 = idle;
		} else if (robot.equals(robot2)) {
			idle2 = idle;
		} else {
			idle3 = idle;
		}

	}

	/**
	 * Makes lists of commands for the robots
	 */

	public void computeCommands() {

		int rekt = 0;

		// work on each robot at the same time, so we get all the job queues
		// these queues are split into parts the robot can carry at once
		// so there is no need to visit the base inside an iteration
		// the robots go collection - base - collection - base etc

		ArrayList<ItemCollection> queue1 = weightedJobs.get(robot1);
		ArrayList<ItemCollection> queue2 = weightedJobs.get(robot2);
		ArrayList<ItemCollection> queue3 = weightedJobs.get(robot3);

		// this is such a ghetto arrayList
		// there's probably a better way to do this

		@SuppressWarnings("unchecked")
		ArrayList<Junction>[] reserveTable = (ArrayList<Junction>[]) new ArrayList<?>[timeWindow];

		for (int i = 0; i < timeWindow; i++) {
			reserveTable[i] = new ArrayList<Junction>();
		}

		// Right, this is how this works
		// Each robot has already been assigned a job queue (above)
		// These queues may or may not be different lengths
		// We should iterate one job at a time
		// finder.findRoute gets an n step path for each robot
		// (I keep thinking it's ten but this can change)
		// It will return null if the start = goal
		// Some collections may take longer than ten steps
		// Other robots should wait if they are finished
		// Once every robot is done, proceed to next job
		// (this will be a base return job)
		// IMPORTANT: there may be less bases than robots
		// This may cause a problem, will have to be looked at
		// Hopefully there will be more bases than robots

		// Find out which robot has the most number of stops
		// (i.e. the most item collections)
		// This is the number of overall iterations
		// (some of these iterations may only do one robot)
		// Each of these overall iterations will cycle through the job queue
		// for each robot and find the path to each item in it

		double longestList = Math.max(queue1.size(), Math.max(queue2.size(), queue3.size()));

		for (int j = 0; j < longestList * 2; j++) {

			// every other iteration will be a picking iteration
			// every other iteration will be a base iteration

			if (j % 2 == 0) {

				base1 = false;
				base2 = false;
				base3 = false;

				// we now focus on the jth TtemCollection of each robot
				// first make some empty ones to use later

				ItemCollection job1 = new ItemCollection();
				ItemCollection job2 = new ItemCollection();
				ItemCollection job3 = new ItemCollection();

				// if there is a jth collection, get it!
				// if not flag that robot as completed

				if (j < queue1.size()) {
					job1 = queue1.get(j);
				} else {
					idle1 = true;
				}

				if (j < queue2.size()) {
					job2 = queue2.get(j);
				} else {
					idle2 = true;
				}

				if (j < queue3.size()) {
					job3 = queue3.get(j); // get the jth for robot3
				} else {
					idle3 = true;
				}

				// now to get the list of items from each collection
				// these lists will not exceed the maximum weight
				// therefore they can all be performed in one fell swoop

				ArrayList<ItemQuantity> items1 = job1.getCollection();
				ArrayList<ItemQuantity> items2 = job2.getCollection();
				ArrayList<ItemQuantity> items3 = job3.getCollection();

				// again find the longest of these lists
				// we'll be iterating over each one at the same time
				// finished robots will wait while the others are moving

				double longestListItem = Math.max(items1.size(), Math.max(items2.size(), items3.size()));

				for (int k = 0; k < longestListItem; k++) {

					// this is where it gets complicated

					// three null items ready to assign

					Item item1 = null;
					Item item2 = null;
					Item item3 = null;

					// some of the collections have less items than others

					if (k <= items1.size() - 1) {
						item1 = items1.get(k).getItem();
					} else {
						idle1 = true;
					}

					if (k <= items2.size() - 1) {
						item2 = items2.get(k).getItem();
					} else {
						idle2 = true;
					}

					if (k <= items3.size() - 1) {
						item3 = items3.get(k).getItem();
					} else {
						idle3 = true;
					}

					// the basic idea is that each robot takes its turn
					// according to its priority and finds a route

					// currently using extremely basic implementation
					// each robot gets a go doing its bit of the route
					// if the returned package is null (i.e. goal reached)
					// a flag is set and pick is added to commands
					// this robot then sits still until the other items are
					// found

					//

					while (!idle1 || !idle2 || !idle3) {

						// robot1 - highest priority

						if (!idle1) {

							findTheRoute(map.getJunction((int) robot1.getX(), (int) robot1.getY()),
									map.getJunction((int) item1.getX(), (int) item1.getY()), reserveTable, robot1,
									idle1, base1);

						} else {
							for (int wait = 0; wait < timeWindow - 1; wait++) {
								pairedCommands.get(robot1).addCommand(Command.WAIT);
								reserveTable[wait].add(map.getJunction((int) robot1.getX(), (int) robot1.getY()));

							}
						}

						// robot2 - second priority

						if (!idle2) {

							System.out.println("ITEM: (" + robot2.getX() + ", " + robot2.getY() + ") to ("
									+ item2.getX() + ", " + item2.getY() + ")");
							findTheRoute(map.getJunction((int) robot2.getX(), (int) robot2.getY()),
									map.getJunction((int) item2.getX(), (int) item2.getY()), reserveTable, robot2,
									idle2, base2);

						} else {
							for (int wait = 0; wait < timeWindow - 1; wait++) {
								pairedCommands.get(robot2).addCommand(Command.WAIT);
								reserveTable[wait].add(map.getJunction((int) robot2.getX(), (int) robot2.getY()));
							}
						}
						// robot3 - lowest priority

						if (!idle3) {

							findTheRoute(map.getJunction((int) robot3.getX(), (int) robot3.getY()),
									map.getJunction((int) item3.getX(), (int) item3.getY()), reserveTable, robot3,
									idle3, base3);
						} else {
							for (int wait = 0; wait < timeWindow - 1; wait++) {
								pairedCommands.get(robot3).addCommand(Command.WAIT);
								reserveTable[wait].add(map.getJunction((int) robot3.getX(), (int) robot3.getY()));
							}
						}

						// System.out.println(pairedCommands.get(robot2).getCommands());
						rekt++;

						for (int p = 0; p < timeWindow; p++) {
							reserveTable[p] = new ArrayList<>();
						}
					}
				}
			} else {

				base1 = true;
				base2 = true;
				base3 = true;

				idle1 = false;
				idle2 = false;
				idle3 = false;

				Junction baseJ1 = findBase(map.getJunction((int) robot1.getX(), (int) robot1.getY()), robot1);
				Junction baseJ2 = findBase(map.getJunction((int) robot2.getX(), (int) robot2.getY()), robot2);
				Junction baseJ3 = findBase(map.getJunction((int) robot3.getX(), (int) robot3.getY()), robot3);

				// time to go to a base
				// currently just doing based on heuristic
				// i can't be bothered to get more accurate atm

				while (!idle1 || !idle2 || !idle3) {

					if (!idle1) {

						findTheRoute(map.getJunction((int) robot1.getX(), (int) robot1.getY()), baseJ1, reserveTable,
								robot1, idle1, base1);

					} else {
						for (int wait = 0; wait < timeWindow - 1; wait++) {
							pairedCommands.get(robot1).addCommand(Command.WAIT);
							reserveTable[wait].add(map.getJunction((int) robot1.getX(), (int) robot1.getY()));

						}
					}

					if (!idle2) {

						System.out.println("BASE: (" + robot2.getX() + ", " + robot2.getY() + ") to (" + baseJ2 + ")");
						findTheRoute(map.getJunction((int) robot2.getX(), (int) robot2.getY()), baseJ2, reserveTable,
								robot2, idle2, base2);

					} else {
						for (int wait = 0; wait < timeWindow - 1; wait++) {
							pairedCommands.get(robot2).addCommand(Command.WAIT);
							reserveTable[wait].add(map.getJunction((int) robot2.getX(), (int) robot2.getY()));

						}
					}

					if (!idle3) {

						findTheRoute(map.getJunction((int) robot3.getX(), (int) robot3.getY()), baseJ3, reserveTable,
								robot3, idle3, base3);

					} else {
						for (int wait = 0; wait < timeWindow - 1; wait++) {
							pairedCommands.get(robot3).addCommand(Command.WAIT);
							reserveTable[wait].add(map.getJunction((int) robot3.getX(), (int) robot3.getY()));

						}
					}

					rekt++;

				}

			}
		}

	}

	/**
	 * Attempt to find the route between two points, keeping in a time frame
	 * 
	 * @param goal
	 *            the goal
	 * @param reserveTable
	 *            the reserve table
	 * @param robot
	 *            the robot
	 */

	private void findTheRoute(Junction start, Junction goal, ArrayList<Junction>[] reserveTable, Robot robot,
			boolean idle, boolean base) {

		RoutePackage rPackage = new RoutePackage();
		Direction direction = robot.getDirection();

		rPackage = finder.findRoute(map.getJunction((int) robot.getX(), (int) robot.getY()), goal, robot.getDirection(),
				reserveTable);

		if (rPackage == null) {
			setState(robot, true);

			if (!base) {
				pairedCommands.get(robot).addCommand(Command.PICK);
			} else {
				pairedCommands.get(robot).addCommand(Command.DROP);
			}

			for (int wait = 0; wait < timeWindow - 1; wait++) {

				if (wait != 0) {
					pairedCommands.get(robot).addCommand(Command.WAIT);
				}

				reserveTable[wait].add(map.getJunction((int) robot.getX(), (int) robot.getY()));
			}

		} else {
			ArrayList<Direction> directList = rPackage.getDirectionList();
			LinkedList<Command> list = rPackage.getCommandList();
			ArrayList<Junction> junctionList = rPackage.getJunctionList();

			pairedCommands.get(robot).addCommandList(list);

			Junction end = null;

			try {
				end = junctionList.get(junctionList.size() - 1);
			} catch (ArrayIndexOutOfBoundsException e) {
				end = start;
			}
			
			robot.setX(end.getX());
			robot.setY(end.getY());

			try {
				robot.setDirection(directList.get(directList.size() - 1));
			} catch (ArrayIndexOutOfBoundsException e) {
				robot.setDirection(direction);
			}
			
			if (junctionList.size() < timeWindow - 1) {
				for (int len = junctionList.size() - 1; len < timeWindow; len++) {
					pairedCommands.get(robot).addCommand(Command.WAIT);
					
					if(len != -1){
					reserveTable[len].add(map.getJunction((int) robot.getX(), (int) robot.getY()));
					}
				}
			}
		}
	}

	/**
	 * Find the 'closest' base (currently not optimal)
	 * 
	 * @param start
	 *            the start position
	 * @return the base
	 */

	private Junction findBase(Junction start, Robot robot) {

		Junction closest = new Junction(0, 0);
		int lowest = map.getHeight() + map.getWidth();

		for (Entry<Junction, Robot> entry : baseStatus.entrySet()) {
			if (entry.getValue() == null) {
				int heury = 0;
				if ((heury = finder.getHeuristic(map.getJunction((int) start.getX(), (int) start.getY()),
						entry.getKey())) < lowest) {
					closest = entry.getKey();
					lowest = heury;
				}
			}
		}

		baseStatus.put(closest, robot);
		return closest;
	}

}
