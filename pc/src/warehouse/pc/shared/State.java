package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.Arrays;

import warehouse.shared.robot.Robot;

public class State {
	private Map map;
	private ArrayList<Robot> robots;
	
	public State(Map _map) {
		this.map = _map;
		this.robots = new ArrayList<>();
	}
	public State(Map _map, ArrayList<Robot> _robots) {
		this.map = _map;
		this.robots = new ArrayList<>();
		
	    for(Robot r : _robots) {
	        if (!robots.contains(r)) {
	            robots.add(r);
	        }
	    }
	}
	
	public State(Map _map, Robot[] _robots) {
		this(_map, new ArrayList<>(Arrays.asList(_robots)));
	}

	public Robot[] getRobots() {
		return robots.toArray(new Robot[robots.size()]);
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
			}
		}
	}
	
	public Map getMap() {
		return map;
	}
}
