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
	
	private ArrayList<CState> successors = null;
	
	public CState(Map _map, RouteFinder _finder, CReserveTable _reserve, int _robotX, int _robotY, int _time) {
		map = _map;
		finder = _finder;
		reserve = _reserve;
		robotX = _robotX;
		robotY = _robotY;
		time = _time;
	}
	
	public CState(CState _parent, CommandType _command) {
		this(_parent.map, _parent.finder, _parent.reserve,
			transformX(_parent.robotX, _command), transformY(_parent.robotY, _command), _parent.time + 1);
		parent = _parent;
		command = _command;
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
	private static int transformY(int x, CommandType c) {
		switch (c) {
		case Y_POS:
			return x + 1;
		case Y_NEG:
			return x - 1;
		default:
			return x;
		}
	}
	
	private Junction getJunction(int x, int y, Direction d) {
		Junction j = map.getJunction(x, y);
		if (j == null)
			return null;
		if (reserve.isPositionReserved(j, time + 1)) // Check for reserved positions.
			return null;
		return j.getNeighbours()[d.ordinal()];
	}
	
	public ArrayList<CState> getSucessors() {
		if (successors == null) {
			successors = new ArrayList<>();
			
			// For each direction, add the sucessor state
			for (Direction d : Direction.values()) {
				Junction j = getJunction(robotX, robotY, d);
				if (j != null)
					successors.add(new CState(this, CommandType.fromDirection(d)));
			}
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
	public int hashCode() {
		return robotX * 5021923
			+ robotY * 103 + 1275
			+ time * 155891;
	}

	public int getTime() {
		return time;
	}
}
