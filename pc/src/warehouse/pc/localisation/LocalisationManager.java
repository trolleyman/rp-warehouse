package warehouse.pc.localisation;

import warehouse.pc.shared.DistanceListener;
import warehouse.shared.robot.Robot;

public class LocalisationManager implements DistanceListener {
	public LocalisationManager() {
		
	}

	@Override
	public void distanceRecieved(Robot _robot, int _dist) {
		// TODO Update probability matrix
		// TODO Detect whenever a robot is not where it should be
	}
}
