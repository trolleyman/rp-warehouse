package warehouse.pc.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import warehouse.shared.robot.Robot;

/**
 * Assigns jobs to robot that request them
 */
public class JobHandler {

	private JobSelector selector;
	private HashMap<Robot, LinkedList<Job>> map;
	
	/**
	 * Initialises the Job Selector with the location of the csv files
	 * The locations should be "locations.csv", "items.csv", "jobs.csv", "drops.csv" in that order
	 */
	public JobHandler(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation){
		selector = new JobSelector(locationsLocation, itemsLocation, jobsLocation, dropsLocation);
		this.map = new HashMap<Robot, LinkedList<Job>>();
	}
	
	/**
	 * Creates a map of robot to queue of jobs.
	 * @param robots Array of robots to be used
	 * @return Map
	 */
	public HashMap<Robot, LinkedList<Job>> createJobMap(Robot[] robots) {
		//Here is where jobs will be assigned.
		//Temporarily for now, jobs will just be taken from the list and given to each robot.
		
		//Inititate job queue for each robot.
		ArrayList<LinkedList<Job>> jobQueues = new ArrayList<LinkedList<Job>>();
		for (int i = 0; i < robots.length; i++) {
			jobQueues.add(new LinkedList<Job>());
		}
		
		//Add jobs to each queue.
		boolean endOfQueue = false;
		while (!endOfQueue) {
			for (int i = 0; i < selector.numberOfJobs(); i++) {
				// TODO using dummy values
				if (selector.getJob(0, 0, 50).isPresent()) {
					jobQueues.get(i % robots.length).offer(selector.getJob(0, 0, 50).get());
				} else {
					endOfQueue = true;
				}
			}
		}
		
		//Add queues to map.
		for (int i = 0; i < robots.length; i++) {
			map.put(robots[i], jobQueues.get(i));
		}
		
		//Return map
		return this.getMap();
	}
	
	/**
	 * Returns the map linking robots to queues of jobs.
	 */
	public HashMap<Robot, LinkedList<Job>> getMap() {
		return this.map;
	}
	
	/**
	 * Assigns a new job to the robot
	 * @param robot The robot that is requesting a job
	 * @param x The x coordinates of the robot
	 * @param y The y coordinates of the robot
	 * @param freeWeight The amount of weight the robot is able to carry
	 */
	public void request(Robot robot, int x, int y, float freeWeight){
		//robot.assign doesn't exist yet
		//robot.assign(selector.getJob(x, y, freeWeight));
	}
	
	public void removeJob(int id){
		selector.remove(id);
	}
}
