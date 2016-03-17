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
			ArrayList<Junction> _dropList) {

		finder = new MultiRouteFinder(_map);
		maxWeight = _maxWeight;
		pairedJobs = _jobs;
		map = _map;

		pairedCommands = new HashMap<Robot, CommandQueue>();

		// make a command queue for every robot, and put them in a hash table

		int i = 0;

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			setUpRobots(entry.getKey(), i);
			pairedCommands.put(entry.getKey(), new CommandQueue());
		}

		bases = _dropList;
		weights = new HashMap<Robot, Float>();

		// set the weight for every robot to be 0 (carrying nothing)

		for (Entry<Robot, CommandQueue> entry : pairedCommands.entrySet()) {

			weights.put(entry.getKey(), 0f);

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

	public void setUpRobots(Robot robot, int i) {

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
	}

	/**
	 * Makes lists of commands for the robots
	 */

	public void computeCommands() {


		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			LinkedList<Job> queue1 = pairedJobs.get(robot1); // getting the
																// queue of jobs
			LinkedList<Job> queue2 = pairedJobs.get(robot2);
			LinkedList<Job> queue3 = pairedJobs.get(robot3);

			// this assumes the queue is already in the order we
			// want the robot to pick them up in

			double longestList = Math.max(queue1.size(), Math.max(queue2.size(), queue3.size()));

			for (int j = 0; j < longestList; j++) {

				Job job1 = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);
				Job job2 = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);
				Job job3 = new Job(0, new ArrayList<ItemQuantity>(), 0, 0);
				
				if (j <= queue1.size()) {
					job1 = queue1.get(j); // get the jth for robot1
				}

				if (j <= queue2.size()) {
					job2 = queue2.get(j); // get the jth for robot2
				}

				if (j <= queue3.size()) {
					job3 = queue3.get(j); // get the jth for robot3
				}

				
				
				ArrayList<ItemQuantity> items1 = job1.getItems();
				ArrayList<ItemQuantity> items2 = job2.getItems();
				ArrayList<ItemQuantity> items3 = job3.getItems();
				
				double longestListItem = Math.max(items1.size(), Math.max(items2.size(), items3.size()));
				
				for (int k = 0; k < longestListItem; k++) {

					@SuppressWarnings("unchecked")
					ArrayList<Junction>[] reserveTable = (ArrayList<Junction>[]) new ArrayList<?>[pairedJobs.size()];
					
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
						quantity3  = items3.get(k).getQuantity();
					}
					
					

					Junction start1 = map.getJunction((int) robot1.getX(), (int) robot1.getY());
					Junction start2 = map.getJunction((int) robot2.getX(), (int) robot2.getY());
					Junction start3 = map.getJunction((int) robot3.getX(), (int) robot3.getY());
					
					Junction goal1 = null;
					Junction goal2 = null;
					Junction goal3 = null;
					
					
					Direction facing1 = robot1.getDirection();
					Direction facing2 = robot2.getDirection();
					Direction facing3 = robot3.getDirection();
					
					// robot1 - highest priority
					
					if(weights.get(robot1) + quantity1 * item1.getWeight() > maxWeight){
						goal1 = findClosestBase(start1, facing1);
					}
					
					RoutePackage rPackage = finder.findRoute(start1, goal1, facing1, reserveTable);
					ArrayList<Direction> directList = rPackage.getDirectionList();
					LinkedList<Command> list = rPackage.getCommandList();
					ArrayList<Junction> junctionList = rPackage.getJunctionList();

					for(int o = 0; o < reserveTable.length; o++){
						
						
						reserveTable[o].set(o, junctionList.get(o));
						
					}
					
					
					
					
					
					// if adding that item would make the robot carry more than
					// the max weight
					// go to the nearest base instead and repeat this iteration

					if (weights.get(robot) + quantity * item.getWeight() > maxWeight) {

						facing = robot.getDirection();
						goal = findClosestBase(start, facing);

						RoutePackage rPackage = finder.findRoute(start, goal, facing);
						directList = rPackage.getDirections();
						list = rPackage.getCommandList();

						weights.put(robot, 0f);
						/*
						 * System.out.println("base: " + start + " to " + goal);
						 * System.out.println(directList);
						 * System.out.println(list);
						 */
						pairedCommands.get(robot).addCommandList(list);
						pairedCommands.get(robot).addCommand(Command.DROP);

						// this should be updated by the robot, here for testing
						// purposes

						robot.setX(goal.getX());
						robot.setY(goal.getY());

						if (directList.size() != 0) {
							robot.setDirection(directList.get(directList.size() - 1));
						}

						start = goal;

					}

					facing = robot.getDirection();
					goal = item.getJunction();

					RoutePackage itemPackage = finder.findRoute(start, goal, facing);
					directList = itemPackage.getDirections();
					list = itemPackage.getCommandList();

					Float newWeight = weights.get(robot) + quantity * item.getWeight();
					weights.put(robot, newWeight);
					/*
					 * System.out.println("item: " + start + " to " + goal);
					 * System.out.println(directList); System.out.println(list);
					 */
					pairedCommands.get(robot).addCommandList(list);
					pairedCommands.get(robot).addCommand(Command.pickUp(quantity, item.getWeight()));

					// this should be updated by the robot, here for testing
					// purposes

					robot.setX(goal.getX());
					robot.setY(goal.getY());

					if (directList.size() != 0) {
						robot.setDirection(directList.get(directList.size() - 1));
					}

				}

			}

			// robot has done its last job and must go home

			Direction facing = robot.getDirection();

			Junction start = map.getJunction((int) robot.getX(), (int) robot.getY());
			Junction goal = findClosestBase(start, facing);
			RoutePackage homePackage = finder.findRoute(start, goal, facing);
			ArrayList<Direction> directList = homePackage.getDirections();
			LinkedList<Command> list = homePackage.getCommandList();

			/*
			 * System.out.println("home: " + start + " to " + goal);
			 * System.out.println(directList); System.out.println(list);
			 */
			pairedCommands.get(robot).addCommandList(list);
			pairedCommands.get(robot).addCommand(Command.DROP);

			robot.setX(goal.getX());
			robot.setY(goal.getY());

			if (directList.size() != 0) {
				robot.setDirection(directList.get(directList.size() - 1));
			}

		}

	}

	private Junction findClosestBase(Junction start, Direction facing) {
		Junction closestBase = bases.get(0);
		int steps = map.getHeight() + map.getWidth();
		ArrayList<Direction> list = new ArrayList<Direction>();

		for (int l = 0; l < bases.size(); l++) {

			RoutePackage basePackage = finder.findRoute(start, bases.get(l), facing);
			list = basePackage.getDirections();

			if (list.size() < steps) {
				closestBase = bases.get(l);
				steps = list.size();
			}

		}

		return closestBase;
	}
}
