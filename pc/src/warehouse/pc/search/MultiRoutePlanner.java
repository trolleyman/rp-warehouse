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

		pairedCommands = new HashMap<Robot, CommandQueue>();
		weightedJobs = new HashMap<Robot, ArrayList<ItemCollection>>();

		bases = _dropList;
		weights = new HashMap<Robot, Float>();

		setUpJobs();

		for (Entry<Robot, ArrayList<ItemCollection>> entry : weightedJobs.entrySet()) {
			System.out.println(entry.getValue());
		}

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

		i++;

	}

	/**
	 * Split the jobs into collections the robot can manage at once
	 */

	private void setUpJobs() {

		// make a command queue for every robot, and put them in a hash table

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			int i = 0;
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

	/**
	 * Makes lists of commands for the robots
	 */

	public void computeCommands() {

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

		// right, this is how this works
		// each robot has already been assigned a job queue (above)
		// these queues may or may not be different lengths
		// we should iterate one job at a time
		// finder.findRoute gets an n step path for each robot
		// (I keep thinking it's ten but this can change)
		// it will return null if the start = goal
		// this might not be enough to cover all the collections
		// if every job is finished, great, carry on
		// if not then call findRoute again for each robot
		// other robots should wait if they are finished
		// once every robot is done, proceed to next job
		// (this will be a base return job)
		// IMPORTANT: there may be less bases than robots
		// this may cause a problem, will have to be looked at

		// find out which robot has the most number of stops

		double longestList = Math.max(queue1.size(), Math.max(queue2.size(), queue3.size()));

		for (int j = 0; j < longestList; j++) {

			ItemCollection job1 = new ItemCollection();
			ItemCollection job2 = new ItemCollection();
			ItemCollection job3 = new ItemCollection();

			if (j <= queue1.size()) {
				job1 = queue1.get(j); // get the jth for robot1
			}

			if (j <= queue2.size()) {
				job2 = queue2.get(j); // get the jth for robot2
			}

			if (j <= queue3.size()) {
				job3 = queue3.get(j); // get the jth for robot3
			}

			ArrayList<ItemQuantity> items1 = job1.getCollection();
			ArrayList<ItemQuantity> items2 = job2.getCollection();
			ArrayList<ItemQuantity> items3 = job3.getCollection();

			double longestListItem = Math.max(items1.size(), Math.max(items2.size(), items3.size()));

			for (int k = 0; k < longestListItem; k++) {

				// this is really dodgy, feck it I'm George Kaye
				// did Martin tell us not to do this?

				// these three arraylists hold the spaces for each robot,
				// they will be added to the reserveTable array as they are
				// filled

				ArrayList<Junction> spaces1 = new ArrayList<>();
				ArrayList<Junction> spaces2 = new ArrayList<>();
				ArrayList<Junction> spaces3 = new ArrayList<>();

				reserveTable[0] = spaces1;
				reserveTable[1] = spaces2;
				reserveTable[2] = spaces3;

				Item item1 = null;
				Item item2 = null;
				Item item3 = null;
				int quantity1 = 0;
				int quantity2 = 0;
				int quantity3 = 0;
				boolean moved1 = false;
				boolean moved2 = false;
				boolean moved3 = false;
				int steps1 = 0;
				int steps2 = 0;
				int steps3 = 0;

				if (k <= items1.size()) {
					item1 = items1.get(k).getItem();
					quantity1 = items1.get(k).getQuantity();
				}

				if (k <= items2.size()) {
					item2 = items2.get(k).getItem();
					quantity2 = items2.get(k).getQuantity();
				}

				if (k <= items3.size()) {
					item3 = items1.get(k).getItem();
					quantity3 = items3.get(k).getQuantity();
				}

				Junction start1 = map.getJunction((int) robot1.getX(), (int) robot1.getY());
				Junction start2 = map.getJunction((int) robot2.getX(), (int) robot2.getY());
				Junction start3 = map.getJunction((int) robot3.getX(), (int) robot3.getY());

				reserveTable[0].set(0, start1);
				reserveTable[1].set(1, start2);
				reserveTable[2].set(2, start3);

				Junction goal1 = null;
				Junction goal2 = null;
				Junction goal3 = null;

				Direction facing1 = robot1.getDirection();
				Direction facing2 = robot2.getDirection();
				Direction facing3 = robot3.getDirection();

				ArrayList<Junction> junctionList1 = new ArrayList<>();
				ArrayList<Junction> junctionList2 = new ArrayList<>();
				ArrayList<Junction> junctionList3 = new ArrayList<>();

				// robot1 - highest priority

				if (item1 != null) {

					if (weights.get(robot1) + quantity1 * item1.getWeight() > maxWeight) {
						goal1 = findClosestBase(start1, facing1);
					}

					RoutePackage rPackage1 = finder.findRoute(start1, goal1, facing1, reserveTable);
					ArrayList<Direction> directList1 = rPackage1.getDirectionList();
					LinkedList<Command> list1 = rPackage1.getCommandList();
					junctionList1 = rPackage1.getJunctionList();

					pairedCommands.get(robot1).addCommandList(list1);
					pairedCommands.get(robot1).addCommand(Command.PICK);

					for (int a = 0; a < junctionList1.size(); a++) {

						reserveTable[0].set(a, junctionList1.get(a));

					}
				}

				// robot2 - second priority

				if (item2 != null) {

					if (weights.get(robot2) + quantity2 * item2.getWeight() > maxWeight) {
						goal2 = findClosestBase(start2, facing2);

					}

					RoutePackage rPackage2 = finder.findRoute(start2, goal2, facing2, reserveTable);
					ArrayList<Direction> directList2 = rPackage2.getDirectionList();
					LinkedList<Command> list2 = rPackage2.getCommandList();
					junctionList2 = rPackage2.getJunctionList();

					pairedCommands.get(robot2).addCommandList(list2);
					pairedCommands.get(robot2).addCommand(Command.PICK);

					for (int a = 0; a < junctionList2.size(); a++) {

						reserveTable[1].set(a, junctionList2.get(a));

						if (a > junctionList1.size()) {
							reserveTable[0].set(a, reserveTable[0].get(a - 1));
							pairedCommands.get(robot1).addCommand(Command.WAIT);
						}

					}
				}

				// robot3 - lowest priority

				if (item3 != null) {

					if (weights.get(robot3) + quantity3 * item3.getWeight() > maxWeight) {
						goal3 = findClosestBase(start3, facing3);

					}

					RoutePackage rPackage3 = finder.findRoute(start3, goal3, facing3, reserveTable);
					ArrayList<Direction> directList3 = rPackage3.getDirectionList();
					LinkedList<Command> list3 = rPackage3.getCommandList();
					junctionList3 = rPackage3.getJunctionList();

					pairedCommands.get(robot3).addCommandList(list3);
					pairedCommands.get(robot3).addCommand(Command.PICK);

					for (int a = 0; a < junctionList3.size(); a++) {

						reserveTable[2].set(a, junctionList3.get(a));

						if (a > junctionList1.size()) {
							reserveTable[0].set(a, reserveTable[0].get(a - 1));
							pairedCommands.get(robot1).addCommand(Command.WAIT);
						}

					}
				}

				// if adding that item would make the robot carry more than
				// the max weight
				// go to the nearest base instead and repeat this iteration

				/*
				 * if (weights.get(robot) + quantity * item.getWeight() >
				 * maxWeight) {
				 * 
				 * facing = robot.getDirection(); goal = findClosestBase(start,
				 * facing);
				 * 
				 * RoutePackage rPackage = finder.findRoute(start, goal,
				 * facing); directList = rPackage.getDirections(); list =
				 * rPackage.getCommandList();
				 * 
				 * weights.put(robot, 0f);
				 * 
				 * System.out.println("base: " + start + " to " + goal);
				 * System.out.println(directList); System.out.println(list);
				 * 
				 * pairedCommands.get(robot).addCommandList(list);
				 * pairedCommands.get(robot).addCommand(Command.DROP);
				 * 
				 * // this should be updated by the robot, here for testing //
				 * purposes
				 * 
				 * robot.setX(goal.getX()); robot.setY(goal.getY());
				 * 
				 * if (directList.size() != 0) {
				 * robot.setDirection(directList.get(directList.size() - 1)); }
				 * 
				 * start = goal;
				 * 
				 * }
				 * 
				 * facing = robot.getDirection(); goal = item.getJunction();
				 * 
				 * RoutePackage itemPackage = finder.findRoute(start, goal,
				 * facing); directList = itemPackage.getDirections(); list =
				 * itemPackage.getCommandList();
				 * 
				 * Float newWeight = weights.get(robot) + quantity *
				 * item.getWeight(); weights.put(robot, newWeight);
				 * 
				 * System.out.println("item: " + start + " to " + goal);
				 * System.out.println(directList); System.out.println(list);
				 * 
				 * pairedCommands.get(robot).addCommandList(list);
				 * pairedCommands.get(robot).addCommand(Command.pickUp(quantity,
				 * item.getWeight()));
				 * 
				 * // this should be updated by the robot, here for testing //
				 * purposes
				 * 
				 * robot.setX(goal.getX()); robot.setY(goal.getY());
				 * 
				 * if (directList.size() != 0) {
				 * robot.setDirection(directList.get(directList.size() - 1)); }
				 * 
				 * }
				 * 
				 * }
				 * 
				 * // robot has done its last job and must go home
				 * 
				 * Direction facing = robot.getDirection();
				 * 
				 * Junction start = map.getJunction((int) robot.getX(), (int)
				 * robot.getY()); Junction goal = findClosestBase(start,
				 * facing); RoutePackage homePackage = finder.findRoute(start,
				 * goal, facing); ArrayList<Direction> directList =
				 * homePackage.getDirections(); LinkedList<Command> list =
				 * homePackage.getCommandList();
				 * 
				 * 
				 * System.out.println("home: " + start + " to " + goal);
				 * System.out.println(directList); System.out.println(list);
				 * 
				 * pairedCommands.get(robot).addCommandList(list);
				 * pairedCommands.get(robot).addCommand(Command.DROP);
				 * 
				 * robot.setX(goal.getX()); robot.setY(goal.getY());
				 * 
				 * if (directList.size() != 0) {
				 * robot.setDirection(directList.get(directList.size() - 1)); }
				 */

			}
		}

	}

	private Junction findClosestBase(Junction start, Direction facing) {
		Junction closestBase = bases.get(0);
		int steps = map.getHeight() + map.getWidth();
		ArrayList<Direction> list = new ArrayList<Direction>();

		for (int l = 0; l < bases.size(); l++) {

			// RoutePackage basePackage = finder.findRoute(start, bases.get(l),
			// facing);
			// list = basePackage.getDirections();

			if (list.size() < steps) {
				closestBase = bases.get(l);
				steps = list.size();
			}

		}

		return closestBase;
	}
}
