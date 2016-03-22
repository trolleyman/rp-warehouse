package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
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

		oneFinder = new RouteFinder(map);
		finder = new MultiRouteFinder(map);

		setUp();

	}

	/**
	 * Sets up the various hashmaps and makes sure it doesn't break
	 * It probably will break though
	 */
	
	private void setUp() {

		for (Entry<Robot, LinkedList<Job>> entry : pairedJobs.entrySet()) {

			// filling the various hashmaps

			idle.put(entry.getKey(), false);
			pairedCommands.put(entry.getKey(), new CommandQueue());

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

}
