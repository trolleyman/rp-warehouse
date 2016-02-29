package warehouse.pc.job;

import warehouse.shared.robot.Robot;

/**
 * Assigns jobs to robot that request them
 */
public class JobHandler {

	private JobSelector selector;
	
	/**
	 * Initialises the Job Selector with the location of the csv files
	 * The locations should be "locations.csv", "items.csv", "jobs.csv", "drops.csv" in that order
	 */
	public JobHandler(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation){
		selector = new JobSelector(locationsLocation, itemsLocation, jobsLocation, dropsLocation);
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
		robot.assign(selector.getJob(x, y, freeWeight));
	}
	
	public void removeJob(int id){
		selector.remove(id);
	}
}
