package warehouse.pc.shared;

import warehouse.shared.robot.Robot;

public interface DistanceListener {
	public void distanceRecieved(Robot _robot, int _dist);
}
