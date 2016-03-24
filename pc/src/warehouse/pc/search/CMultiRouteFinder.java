package warehouse.pc.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.PriorityQueue;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.shared.Direction;

// Tries to implement Hierarchical Cooperative A*
public class CMultiRouteFinder {
	private Map map;
	private RouteFinder finder;
	
	public CMultiRouteFinder(Map _map, RouteFinder _finder) {
		map = _map;
		finder = _finder;
	}
	
	public Optional<LinkedList<Command>> findRoute(Junction start, Junction goal, CReserveTable reserve, int startTime) {
		HashSet<Junction> closedList = new HashSet<>();
		PriorityQueue<CState> openList = new PriorityQueue<>(new Comparator<CState>() {
			@Override
			public int compare(CState s1, CState s2) {
				int hdiff = getHeuristic(new Junction(s1.getX(), s1.getY()), goal)
					- getHeuristic(new Junction(s2.getX(), s2.getY()), goal);
				if (hdiff != 0) {
					return hdiff;
				} else { // Use time to resolve ties
					return s1.getTime() - s2.getTime();
				}
			}
		});
		
		openList.add(new CState(map, finder, reserve, start.getX(), start.getY(), startTime));
		
		// TODO: This probably fails when the robot cannot get to the goal, as
		// it will keep on generating successors that wait. Maybe a counter that
		// keeps track of this and stops when it has waited more than 10 times?
		while (!openList.isEmpty()) {
			CState current = openList.remove();
			if (current.getX() == goal.getX() && current.getY() == goal.getY()) {
				// Goal has been found, return list of commands
				return Optional.of(current.getCommands());
			}
			
			// Add sucessors of current to open list.
			ArrayList<CState> sucessors = current.getSucessors();
			for (CState s : sucessors) {
				if (!closedList.contains(new Junction(s.getX(), s.getY()))) {
					openList.add(s);
				}
			}
			
			closedList.add(new Junction(current.getX(), current.getY()));
		}
		
		return Optional.empty();
	}
	
	private int getHeuristic(Junction start, Junction end) {
		RoutePackage rp = finder.findRoute(start, end, Direction.Y_POS);
		if (rp == null)
			return Integer.MAX_VALUE;
		LinkedList<Command> cl = rp.getCommandList();
		if (cl == null)
			return Integer.MAX_VALUE;
		return cl.size();
	}
}
