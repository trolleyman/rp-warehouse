package warehouse.pc.shared;

import warehouse.pc.shared.Robot;

/**
 * Defines an interface whose methods are called when a robot is updated.
 */
public interface RobotListener {
	public void robotAdded(Robot _r);
	public void robotRemoved(Robot _r);
	public void robotChanged(Robot _r);
	public void robotAdded(Robot _r);
	public void robotRemoved(Robot _r);
}
