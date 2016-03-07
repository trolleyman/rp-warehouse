package warehouse.pc.shared;

/**
 * A RobotManager will be responsible for managing all of the robots in the system.
 * It will calculate the route planning for all of the robots, send each command to each robot
 * and update MainInterface with the new states of the robots.
 */
public interface IRobotManager extends Runnable {
	/**
	 * Starts the RobotManager
	 */
	@Override
	public void run();
	
	/**
	 * Stops the TobotManager
	 */
	public void stop();
	
	/**
	 * Pauses the RobotManager, allowing for resuming.
	 */
	public void pause();
	
	/**
	 * Resumes the RobotManager. This will also call recalculate().
	 */
	public void resume();
	
	/**
	 * Causes the RobotManager to recalculate it's plans
	 */
	public void recalculate();
}
