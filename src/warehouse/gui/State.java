package warehouse.gui;

import warehouse.shared.Robot;

public class State {
	private Map map;
	private Robot[] robots;
	
	public State(Map _map, Robot[] _robots) {
		this.map = _map;
		this.robots = _robots;
	}
	
	public Robot[] getRobots() {
		return robots;
	}
	
	public Map getMap() {
		return map;
	}
}
