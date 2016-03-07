package warehouse.pc.shared;

import warehouse.shared.robot.Robot;

public interface ILocalisationManager extends DistanceListener, RobotListener {
	public double getProbability(Robot _robot, int _x, int _y);
}
