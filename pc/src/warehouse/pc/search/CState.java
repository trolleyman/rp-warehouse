package warehouse.pc.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandType;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.shared.Direction;

public class CState {
	// The maximum number of times the robot can just wait in one place.
	// Increasing this has a large impact on the running time.
	private static final int MAX_WAITS = 3;
	// The maximum number of times the robot can visit the same junction.
	private static final int MAX_VISITS = 1;
	// The maximum time that the route finder will look ahead.
	// i.e. if a route can't be found in this time, it will not be found.
	private static final int MAX_TIME = 25;
	
	private Map map;
	private RouteFinder finder;
	private CReserveTable reserve;
	private int robotX;
	private int robotY;
	
	private int time;
	
	// Can be null for base parent.
	private CState parent;
	// Command type can be null.
	private CommandType command;
	
	private int numWaits;
		
	public CState(Map _map, RouteFinder _finder, CReserveTable _reserve, int _robotX, int _robotY, int _time) {
		map = _map;
		finder = _finder;
		reserve = _reserve;
		robotX = _robotX;
		robotY = _robotY;
		time = _time;
		numWaits = 0;
	}
	
	public CState(CState _parent, CommandType _command) {
		this(_parent.map, _parent.finder, _parent.reserve,
			transformX(_parent.robotX, _command), transformY(_parent.robotY, _command), _parent.time + 1);
		parent = _parent;
		command = _command;
		if (command == CommandType.WAIT) {
			numWaits = _parent.numWaits + 1;
		} else {
			numWaits = 0;
		}
	}
	
	private static int transformX(int x, CommandType c) {
		switch (c) {
		case X_POS:
			return x + 1;
		case X_NEG:
			return x - 1;
		default:
			return x;
		}
	}
	private static int transformY(int y, CommandType c) {
		switch (c) {
		case Y_POS:
			return y + 1;
		case Y_NEG:
			return y - 1;
		default:
			return y;
		}
	}
	
	private static boolean hasVisitedTimes(CState s, int x, int y, int times) {
		int acc = 0;
		while (true) {
			if (x == s.robotX && y == s.robotY) {
				acc += 1;
				if (acc >= times) {
					return true;
				}
			}
			if (s.parent == null) {
				return false;
			} else {
				s = s.parent;
			}
		}
	}
	
	/**
	 * Returns true if x,y has been visited more than a certain amount of times
	 */
	private boolean hasVisitedTimes(int x, int y, int times) {
		return CState.hasVisitedTimes(this, x, y, times);
	}
	
	private Junction getJunction(int x, int y, Direction d) {
		Junction from = map.getJunction(x, y);
		Junction to = map.getJunction(
			transformX(x, CommandType.fromDirection(d)),
			transformY(y, CommandType.fromDirection(d)));
		if (to == null)
			return null;
		if (reserve.isPositionReserved(to, time + 1)) // Check for reserved positions.
			return null;
		if (hasVisitedTimes(to.getX(), to.getY(), MAX_VISITS))
			return null;
		return from.getJunction(d);
	}
	
	public ArrayList<CState> getSucessors() {
		ArrayList<CState> successors = new ArrayList<>();
		if (time >= MAX_TIME) {
			return successors;
		}
		
		// For each direction, add the sucessor state
		for (Direction d : Direction.values()) {
			Junction j = getJunction(robotX, robotY, d);
			if (j != null)
				successors.add(new CState(this, CommandType.fromDirection(d)));
		}
		
		// Try waiting.
		CState s = new CState(this, CommandType.WAIT);
		boolean reserved = reserve.isPositionReserved(new Junction(robotX, robotY), time + 1);
		if (!reserved && s.numWaits <= MAX_WAITS) {
			successors.add(s);
		}
		
		return successors;
	}
	
	public CState getParent() {
		return parent;
	}
	
	public CommandType getCommand() {
		return command;
	}

	public int getX() {
		return robotX;
	}
	public int getY() {
		return robotY;
	}
	
	private void addCommands(List<Command> commands) {
		if (parent != null) {
			commands.add(new Command(command));
			parent.addCommands(commands);
		}
	}
	
	/**
	 * Gets the commands that led to this state
	 */
	public LinkedList<Command> getCommands() {
		LinkedList<Command> commands = new LinkedList<>();
		addCommands(commands);
		Collections.reverse(commands);
		return commands;
	}
	
	@Override
	public String toString() {
		return "t" + time + ": " + robotX + ", " + robotY;
	}
	
	@Override
	public int hashCode() {
		return robotX * 5021923
			+ robotY * 103 + 1275
			+ time * 155891;
	}

	public int getTime() {
		return time;
	}
}
