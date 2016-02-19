package warehouse.pc.shared;

import warehouse.pc.shared.robot.Robot;

/**
 * Defines an interface who's methods are called when a robot is updated.
 */
public interface RobotListener {
	public void robotChanged(Robot _r);
}