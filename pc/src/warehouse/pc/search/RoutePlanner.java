package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Bearing;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.shared.robot.Robot;

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

	public Bearing getNextCommand(Robot _robot) {
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
	 * Makes lists of commands for the robots
	 */

	public void computeCommands() {

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			Robot robot = entry.getKey(); // getting the first robot

			LinkedList<Job> queue = entry.getValue(); // getting the queue of jobs

			// this assumes the queue is already in the order we
			// want the robot to pick them up in

			for (int j = 0; j < queue.size(); j++) {

				Job job = queue.get(j); // get the next job from the job list										// TODO
				
				for (int k = 0; k < job.getItems().size(); k++) {

					//Item item = job.get(k); // get the kth item from the job										// TODO
					Item item = new Item("placeholder", 5f, 5f, 0, 0);				
					
					Junction start = map.getJunction((int)robot.getX(), (int)robot.getY());
					Junction goal = null;
					//Direction facing = robot.getDirection();														// TODO
					Direction facing = Direction.Y_POS;
					LinkedList<Bearing> list = new LinkedList<Bearing>();
					
					// if adding that item would make the robot carry more than the max weight
					// go to the nearest base instead and repeat this iteration
					
					if (weights.get(robot) + item.getWeight() > maxWeight) {

						Junction closestBase = bases.get(0);
						int steps = map.getHeight() + map.getWidth();
						
						for (int l = 0; l < bases.size(); l++){
							
							list = finder.findRoute(start, bases.get(l), facing);
							if(list.size() < steps){
							closestBase = bases.get(l);
							}
							
						}
						
						goal = closestBase;
						weights.put(robot, 0f);
						k--;
						
						
						
					} else {
						goal = item.getJunction();
						list = finder.findRoute(start, goal, facing); // find the route
						Float newWeight = weights.get(robot) + item.getWeight();
						weights.put(robot, newWeight);
					}
					
					pairedCommands.get(robot).addCommandList(list);
					
					

				}

			}

		}

	}

}
