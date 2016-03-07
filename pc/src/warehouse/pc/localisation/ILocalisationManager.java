package warehouse.pc.localisation;

import warehouse.pc.shared.DistanceListener;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.RobotListener;

public interface ILocalisationManager extends DistanceListener, RobotListener {
	public double getProbability(Robot _robot, int _x, int _y);
}
