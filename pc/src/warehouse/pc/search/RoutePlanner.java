package warehouse.pc.search;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.CommandType;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.shared.Direction;

/**
 * Class to create lists of bearings for individual robots to take
 * Uses a lot of placeholders while the job and robot teams get their classes together
 *
 */

public class RoutePlanner {

	private HashMap<Robot, LinkedList<Job>> pairedJobs;
	private HashMap<Robot, CommandQueue> pairedCommands;
	private RouteFinder finder;
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

	public RoutePlanner(Map _map, Float _maxWeight, HashMap<Robot, LinkedList<Job>> _jobs, ArrayList<Junction> _dropList) {
		
		finder = new RouteFinder(_map);
		maxWeight = _maxWeight;
		pairedJobs = _jobs;
		map = _map;
		
		pairedCommands = new HashMap<Robot, CommandQueue>();
		
		// make a command queue for every robot, and put them in a hash table
		
		for(Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()){
			
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
	 * @param _hash the new hashmap
	 */
	
	public void update(HashMap<Robot, LinkedList<Job>> _hash){
		
		this.pairedJobs = _hash;
		
		for(Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()){
			
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

			Robot robot = entry.getKey(); // getting the first robot

			LinkedList<Job> queue = entry.getValue(); // getting the queue of jobs

			// this assumes the queue is already in the order wez
			// want the robot to pick them up in

			for (int j = 0; j < queue.size(); j++) {

				Job job = queue.get(j); // get the next job from the job list
				
				ArrayList<ItemQuantity> items = job.getItems();				// get a list of items in the job
				
				for (int k = 0; k < items.size(); k++) {

					Item item = items.get(k).getItem(); // get the kth item from the job
					int quantity = items.get(k).getQuantity();
					
					Junction start = map.getJunction(robot.getGridX(), robot.getGridY());
					Junction goal = null;
					ArrayList<Direction> directList = new ArrayList<Direction>();
					LinkedList<Command> list = new LinkedList<Command>();
					Direction facing = null;
					
					// if adding that item would make the robot carry more than the max weight
					// go to the nearest base instead and repeat this iteration
					
					if (weights.get(robot) + quantity * item.getWeight() > maxWeight) {

						facing = robot.getDirection();
						goal = findClosestBase(start, facing);
						
						RoutePackage rPackage = finder.findRoute(start, goal, facing);
						directList = rPackage.getDirectionList();
						list = rPackage.getCommandList();
						
						weights.put(robot, 0f);
						/*System.out.println("base: " + start + " to " + goal);
						System.out.println(directList);
						System.out.println(list);*/
						pairedCommands.get(robot).addCommandList(list);
						pairedCommands.get(robot).addCommand(new Command(CommandType.DROP));
						
						// this should be updated by the robot, here for testing purposes
						
						robot.setGridX(goal.getX());
						robot.setGridY(goal.getY());
						robot.setX(goal.getX());
						robot.setY(goal.getY());
						
						if(directList.size() != 0){
							robot.setDirection(directList.get(directList.size() - 1));
						}
						start = goal;
					}
					goal = item.getJunction();

					RoutePackage itemPackage = finder.findRoute(start, goal, facing);
					directList = itemPackage.getDirectionList();
					list = itemPackage.getCommandList();
						
					Float newWeight = weights.get(robot) + quantity * item.getWeight();
					weights.put(robot, newWeight);
					/*System.out.println("item: " + start + " to " + goal);
					System.out.println(directList);
					System.out.println(list);*/
					pairedCommands.get(robot).addCommandList(list);
					pairedCommands.get(robot).addCommand(Command.pickUp(quantity, item.getWeight()));
					
					// this should be updated by the robot, here for testing purposes
					robot.setGridX(goal.getX());
					robot.setGridY(goal.getY());
					robot.setX(goal.getX());
					robot.setY(goal.getY());
					
					if(directList.size() != 0){
						robot.setDirection(directList.get(directList.size() - 1));
					}
					
					

				}

			}

			// robot has done its last job and must go home
			
			Direction facing = robot.getDirection();
			
			Junction start = map.getJunction((int)robot.getX(), (int)robot.getY());
			Junction goal = findClosestBase(start, facing);
			RoutePackage homePackage = finder.findRoute(start, goal, facing);
			ArrayList<Direction> directList = homePackage.getDirectionList();
			LinkedList<Command> list = homePackage.getCommandList();
			
			/*System.out.println("home: " + start + " to " + goal);
			System.out.println(directList);
			System.out.println(list);*/
			pairedCommands.get(robot).addCommandList(list);
			pairedCommands.get(robot).addCommand(new Command(CommandType.DROP));
			pairedCommands.get(robot).addCommand(new Command(CommandType.COMPLETE_JOB)); // Completes the job
			
			robot.setX(goal.getX());
			robot.setY(goal.getY());
			
			if(directList.size() != 0){
			robot.setDirection(directList.get(directList.size() - 1));
			}
			
			
		}

	}


		private Junction findClosestBase(Junction start, Direction facing){
			Junction closestBase = bases.get(0);
			int steps = map.getHeight() + map.getWidth();
			ArrayList<Direction> list = new ArrayList<Direction>();
			
			for (int l = 0; l < bases.size(); l++){
				
				RoutePackage basePackage = finder.findRoute(start, bases.get(l), facing);	
				list = basePackage.getDirectionList();
				
				if(list.size() < steps){
					closestBase = bases.get(l);
					steps = list.size();
				}
		
			}
	
			return closestBase;
		}
}
