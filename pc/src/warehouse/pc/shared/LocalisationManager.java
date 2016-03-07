package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.HashMap;

import warehouse.shared.robot.Robot;

public class LocalisationManager implements ILocalisationManager, RobotListener {
	HashMap<Robot, ArrayList<ArrayList<Double>>> allProbabilities;
	
	Map map;
	
	public LocalisationManager(Map _map, Robot[] robots) {
		this.map = _map;
		
		allProbabilities = new HashMap<>();
		for (Robot robot : robots) {
			allProbabilities.put(robot, initProbabilities());
		}
	}
	
	private ArrayList<ArrayList<Double>> initProbabilities() {
		ArrayList<ArrayList<Double>> probabilities = new ArrayList<>();
		for (int y = 0; y < map.getHeight(); y++) {
			ArrayList<Double> probs = new ArrayList<>();
			for (int x = 0; x < map.getWidth(); x++) {
				probs.add(0.0);
			}
			probabilities.add(probs);
		}
		return probabilities;
	}

	@Override
	public void distanceRecieved(Robot _robot, int _dist) {
		
	}

	@Override
	public double getProbability(Robot _robot, int _x, int _y) {
		return 0; // TODO
	}

	@Override
	public void robotChanged(Robot _r) {
		// Not needed
	}

	@Override
	public void robotAdded(Robot _r) {
		allProbabilities.put(_r, initProbabilities());
	}

	@Override
	public void robotRemoved(Robot _r) {
		allProbabilities.remove(_r);
	}
}
