package warehouse.pc.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;

public class MultiRouteFinder {

	private ArrayList<Junction> nodes; // Stores all junction data from current
	// map
	private ArrayList<Junction> searched; // Closed set, nodes already searched
	private LinkedHashMap<Junction, Integer> frontier; // Frontier, integer
	// value is moves from
	// start node
	private HashMap<Junction, Junction> cameFrom; // Pointers to junctions once
	// path is found

	private Map map;

	/**
	 * Create a new RouteFinder object for a given map
	 * 
	 * @param _map
	 *            the map
	 */

	public MultiRouteFinder(Map _map) {

		// add the junctions from the map to an ArrayList of nodes to be checked
		map = _map;
		nodes = new ArrayList<Junction>();
		for (int i = 0; i < _map.getWidth(); i++) {
			for (int j = 0; j < _map.getHeight(); j++) {
				nodes.add(_map.getJunction(i, j));
			}
		}

	}

	/**
	 * Finds a route between two junctions on the map
	 * 
	 * @param start
	 *            the start junction
	 * @param goal
	 *            the end junction
	 * @param direction
	 *            the initial direction of the robot
	 * @return the ArrayList of directions
	 */

	public RoutePackage findRoute(Junction start, Junction goal, Direction direction,
			ArrayList<Junction>[] reserveTable) {

		start = map.getJunction(start.getX(), start.getY());
		goal = map.getJunction(goal.getX(), goal.getY());
		
		if (!nodes.contains(start) || !nodes.contains(goal)) {
			return null;
		}

		searched = new ArrayList<Junction>();
		frontier = new LinkedHashMap<Junction, Integer>();
		cameFrom = new HashMap<Junction, Junction>();

		frontier.put(start, 0); // Initializes search
		Junction currentJunct = start; // Assigning initial junction as start
										// will immediately exit
										// search loop if already at goal
		int timeStep = 0;

		// Work in progress

		// New loop for WHCA*, removes while frontier is !empty
		// This finder should be run multiple times for each robot until EVERY
		// robot is at its destination, at which point they can drop off, pickup
		// etc. That is, perhaps routeplanner.java should be changed to
		// operate on robots simultaneously, so get each robots goal (one
		// pickup or dropoff), and then execute the finder

		while (((currentJunct.getX() != goal.getX()) || (currentJunct.getY() != goal.getY()))
				&& (timeStep < reserveTable.length)) {
			int minCost = -1;
			int pathEstimate = 0;
			int movesFromStart = 0;

			// Iterate through frontier to find lowest cost junction
			for (Entry<Junction, Integer> entry : frontier.entrySet()) {

				movesFromStart = entry.getValue();
				pathEstimate = movesFromStart + getHeuristic(entry.getKey(), goal);

				if (minCost < 0) {
					minCost = pathEstimate;
					currentJunct = entry.getKey();
				} else if (pathEstimate < minCost) {
					minCost = pathEstimate;
					currentJunct = entry.getKey();
				}
			}

			reserveTable[timeStep].add(currentJunct);

			// if the current junction is the goal return the path

			if ((currentJunct.getX() == goal.getX()) && (currentJunct.getY() == goal.getY())) {

				RoutePackage rPackage = new RoutePackage();
				ArrayList<Direction> directionList = makePath(start, goal, rPackage);
				rPackage.setCommandList(getActualDirections(directionList, direction));
				rPackage.setDirectionList(directionList);

				return rPackage;
			}

			// remove the junction from the frontier and add it to the explored

			frontier.remove(currentJunct);
			searched.add(currentJunct);

			// add adjacent junctions to the frontier

			for (Junction neighbour : currentJunct.getNeighbours()) {

				if ((neighbour == null) || (searched.contains(neighbour))
						|| (reserveTable[timeStep].contains(neighbour))
						|| arrayPlusOne(reserveTable, timeStep, neighbour)) {
					continue;
				} else if (reserveTable[timeStep].contains(neighbour)
						|| arrayPlusOne(reserveTable, timeStep, neighbour))
					// Add wait command NOT IMPLEMENTED

					if (!frontier.containsKey(neighbour)) {
						// For safety
						frontier.put(neighbour, movesFromStart + 1);

					} else if ((movesFromStart + 1) >= frontier.get(neighbour))
						continue;

				frontier.remove(neighbour);
				frontier.put(neighbour, movesFromStart + 1);

				cameFrom.put(neighbour, currentJunct);
			}

			timeStep++;
		}

		if ((start.getX() == goal.getX()) && (start.getY() == goal.getY())) {

			while (timeStep < reserveTable.length) {

				reserveTable[timeStep].add(start);
			}
			return null;
		}

		else {
			RoutePackage rPackage = new RoutePackage();
			ArrayList<Direction> directionList = makePath(start, currentJunct, rPackage);
			
			rPackage.setDirectionList(directionList);
			rPackage.setCommandList(getActualDirections(directionList, direction));

			return rPackage;

		}

	}

	private boolean arrayPlusOne(ArrayList<Junction>[] a, int x, Junction j) {

		try {
			if (a[x + 1].contains(j)) {
				return true;
			} else {
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

	}

	private ArrayList<Direction> makePath(Junction start, Junction current, RoutePackage rPackage) {

		ArrayList<Junction> revPath = new ArrayList<Junction>();

		// if the start junction is not the goal junction

		if ((start.getX() != current.getX()) || (start.getY() != current.getY())) {
			revPath.add(current);
		}

		while ((start.getX() != current.getX()) || (start.getY() != current.getY())) {
			revPath.add(cameFrom.get(current));
			current = cameFrom.get(current);
		}
		
		

		// reverse the list (it currently goes goal -> start)

		Collections.reverse(revPath);
		System.out.println(revPath);

		// convert the list of nodes into a list of directions (relative to
		// north)

		ArrayList<Direction> moveList = new ArrayList<Direction>();

		for (int i = 0; i < revPath.size() - 1; i++) {

			Junction first = revPath.get(i);
			Junction second = revPath.get(i + 1);

			if (second.getX() > first.getX()) {
				moveList.add(Direction.X_POS);
			}

			if (second.getX() < first.getX()) {
				moveList.add(Direction.X_NEG);
			}

			if (second.getY() > first.getY()) {
				moveList.add(Direction.Y_POS);
			}

			if (second.getY() < first.getY()) {
				moveList.add(Direction.Y_NEG);
			}
		}

		rPackage.setJunctionList(revPath);

		System.out.println(moveList);
		
		return moveList;
	}

	public int getHeuristic(Junction current, Junction goal) {
		return (Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY()));
	}

	private LinkedList<Command> getActualDirections(ArrayList<Direction> oldList, Direction direction) {

		LinkedList<Command> newList = new LinkedList<Command>();

		for (int i = 0; i < oldList.size(); i++) {

			Direction currentDirection = oldList.get(i);
			Command bearing = null;

			switch (direction) {
			case Y_POS:
				switch (currentDirection) {
				case Y_POS:
					bearing = Command.FORWARD;
					break;
				case Y_NEG:
					bearing = Command.BACKWARD;
					break;
				case X_POS:
					bearing = Command.RIGHT;
					break;
				case X_NEG:
					bearing = Command.LEFT;
					break;

				}
				break;

			case Y_NEG:
				switch (currentDirection) {
				case Y_NEG:
					bearing = Command.FORWARD;
					break;
				case Y_POS:
					bearing = Command.BACKWARD;
					break;
				case X_NEG:
					bearing = Command.RIGHT;
					break;
				case X_POS:
					bearing = Command.LEFT;
					break;
				}
				break;

			case X_POS:
				switch (currentDirection) {
				case X_POS:
					bearing = Command.FORWARD;
					break;
				case X_NEG:
					bearing = Command.BACKWARD;
					break;
				case Y_NEG:
					bearing = Command.RIGHT;
					break;
				case Y_POS:
					bearing = Command.LEFT;
					break;

				}
				break;

			case X_NEG:
				switch (currentDirection) {
				case X_NEG:
					bearing = Command.FORWARD;
					break;
				case X_POS:
					bearing = Command.BACKWARD;
					break;
				case Y_POS:
					bearing = Command.RIGHT;
					break;
				case Y_NEG:
					bearing = Command.LEFT;
					break;

				}

				break;
			}

			direction = currentDirection;
			newList.add(bearing);

		}

		return newList;
	}
}
