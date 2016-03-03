package warehouse.pc.shared;

import warehouse.shared.robot.Robot;

/**
 * Defines an interface whose methods are called when a robot is updated.
 */
public interface RobotListener {
	public void robotChanged(Robot _r);
}
