package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import warehouse.pc.shared.Robot;

public class State {
	private Map map;
	private HashSet<Robot> robots;
	
	public State(Map _map) {
		this.map = _map;
		this.robots = new HashSet<>();
	}
	public State(Map _map, ArrayList<Robot> _robots) {
		this.map = _map;
		this.robots = new HashSet<>();
	}
	public State(Map _map, HashSet<Robot> _robots) {
		this.map = _map;
		this.robots = _robots;
	}
	
	public State(Map _map, Robot[] _robots) {
		this(_map, new ArrayList<>(Arrays.asList(_robots)));
	}

	public HashSet<Robot> getRobots() {
		return this.robots;
	}
	
	/**
	 * Updates the details for a Robot. If a Robot already exists with the name {@code _r} then this will replace
	 * that Robot. Otherwise {@code _r} will be appended to the list of robots.
	 */
	public void updateRobot(Robot _r) {
		for (Robot robot : robots) {
			if (robot == _r) {
				return;
			}
			if (robot.getName().equals(_r.getName())) {
				robot = _r;
				return;
			}
		}
		robots.add(_r);
	}
	
	public Map getMap() {
		return map;
	}
	
	/**
	 * Removes a robot from the currently tracked robots
	 * @param _r the robot
	 * @return true if the robot was in the array, and has been removed.
	 */
	public boolean removeRobot(Robot _r) {
		return robots.remove(_r);
	}
}
