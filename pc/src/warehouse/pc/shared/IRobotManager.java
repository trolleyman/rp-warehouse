package warehouse.pc.shared;

/**
 * A RobotManager will be responsible for managing all of the robots in the system.
 * It will calculate the route planning for all of the robots, send each command to each robot
 * and update MainInterface with the new states of the robots.
 */
public interface IRobotManager extends Runnable, RobotListener {
	/**
	 * Starts the RobotManager
	 */
	@Override
	public void run();
	
	/**
	 * Causes the RobotManager to recalculate it's plans
	 */
	public void recalculate();
}
