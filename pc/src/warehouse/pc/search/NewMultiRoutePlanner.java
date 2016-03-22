package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;

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

	private HashMap<Robot, Boolean> idle;
	private HashMap<Robot, ArrayList<ItemQuantity>> itemq;

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

			idle.put(priority1, false);
			idle.put(priority2, false);
			idle.put(priority3, false);

			// assign a job to each robot
			// since we're using priorities we need to get it from the hashmap
			// if that robot has run out of jobs set it as idle
			
			
			if (pairedJobs.get(priority1).size() > i/2) {
				jobA = pairedJobs.get(priority1).get(i/2);
			} else {
				idle.put(priority1, true);
			}

			if (pairedJobs.get(priority2).size() > i/2) {
				jobB = pairedJobs.get(priority2).get(i/2);
			} else {
				idle.put(priority2, true);
			}

			if (pairedJobs.get(priority1).size() > i/2) {
				jobC = pairedJobs.get(priority3).get(i/2);
			} else {
				idle.put(priority1, true);
			}
			
			// the jobs will all be different sizes (but hopefully similar sizes)
			// first we need to get the list of items for this iteration
			
			itemq.put(priority1, jobA.getItems());
			itemq.put(priority2, jobB.getItems());
			itemq.put(priority3, jobC.getItems());
			
			System.out.println(itemq.get(priority1));
			System.out.println(itemq.get(priority2));
			System.out.println(itemq.get(priority3));
			
			Robot priorityTemp = priority1;
			priority1 = priority2;
			priority2 = priority3;
			priority3 = priorityTemp;
			
			System.out.println("");
			
			System.out.println(itemq.get(priority1));
			System.out.println(itemq.get(priority2));
			System.out.println(itemq.get(priority3));
			
			
		}

	}

}
