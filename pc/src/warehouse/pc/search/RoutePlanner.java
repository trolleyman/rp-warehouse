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
 * VERY MUCH IN PROGRESS - THERE ARE LOTS OF SQUIGGLY RED LINES EVERYWHERE
 *
 */

public class RoutePlanner {

	private HashMap<Robot, JobQueue> pairedJobs;
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

	public RoutePlanner(Map _map, Float _maxWeight, HashMap<Robot, JobQueue> _jobs, Junction _base, ArrayList<Junction> _dropList) {
		finder = new RouteFinder(_map);
		maxWeight = _maxWeight;
		pairedJobs = _jobs;
		
		pairedCommands = new HashMap<Robot, CommandQueue>();
		
		for(Entry<Robot, JobQueue> entry : pairedJobs.entrySet()){
			
			pairedCommands.put(entry.getKey(), new CommandQueue());
		}
		
		bases = _dropList;

		weights = new HashMap<Robot, Float>();

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

	private void computeCommands() {

		for (Entry<Robot, JobQueue> entry : pairedJobs.entrySet()) {

			Robot robot = entry.getKey(); // getting the first robot

			JobQueue queue = entry.getValue(); // getting the queue of jobs

			// this assumes the queue is already in the order we
			// want the robot to pick them up in

			for (int j = 0; j < queue.size(); j++) {

				Job job = jobList.get(j); // get the jth job from the job list

				for (int k = 0; k < job.size(); k++) {

					Item item = job.get(k); // get the kth item from the job

					Junction start = robot.getPosition();
					Junction goal = null;
					
					
					if (weights.get(robot) + item.getWeight() > maxWeight) {

						goal = base;
						k--;
						
					} else {
						goal = item.getPosition();
					}

					Direction facing = robot.getDirection();

					LinkedList<Bearing> list = finder.findRoute(start, goal, facing); // find the route
					pairedCommands.get(robot).addCommandList(list);
					Float newWeight = weights.get(robot) + item.getWeight();
					weights.put(robot, newWeight);

				}

			}

		}

	}

}
