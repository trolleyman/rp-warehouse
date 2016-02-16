package warehouse.gui;

import warehouse.shared.Robot;

public class State {
	private Map map;
	private Robot[] robots;
	
	public State(Map map, Robot[] robots) {
		this.map = map;
		this.robots = robots;
	}
	
	public Robot[] getRobots() {
		return robots;
	}
	
	public Map getMap() {
		return map;
	}
}
