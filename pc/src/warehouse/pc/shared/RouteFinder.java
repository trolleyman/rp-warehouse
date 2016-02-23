package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import rp.util.Collections;

public class RouteFinder {
	
	private ArrayList<Junction> nodes;                // Stores all junction data from current map
	private ArrayList<Junction> searched;             // Closed set, nodes already searched
	private LinkedHashMap<Junction,Integer> frontier; // Frontier, integer value is moves from start node
	private HashMap<Junction,Junction> cameFrom;      // Pointers to junctions once path is found
	
	public RouteFinder(Map _map) {		
		nodes = new ArrayList<Junction>();
		for (int i = 0; i < _map.getWidth(); i++) {
			for (int j = 0; j < _map.getHeight(); j++) {
				nodes.add(_map.getJunction(i, j));
			}
		}
	}
	
	public ArrayList<Direction> findRoute(Junction start, Junction goal) {
		
		
		if (!nodes.contains(start) || !nodes.contains(goal)) { // Must be valid args
			return null;
		} 
		
		searched = new ArrayList<Junction>();
		frontier = new LinkedHashMap<Junction,Integer>();
		cameFrom = new HashMap<Junction,Junction>();
		frontier.put(start, 0); // Initializes search
		Junction currentJunct = null;
		
		while (!frontier.isEmpty()) {
			int minCost = -1;
			int pathEstimate = 0;
			int movesFromStart = 0;
			
			
			// Iterate through frontier to find lowest cost junction
			for (Entry<Junction, Integer> entry : frontier.entrySet())
			{	

				movesFromStart = entry.getValue();
				pathEstimate = movesFromStart + getHeuristic(entry.getKey(), goal);
				
				if (minCost < 0) {
					minCost = pathEstimate;
					currentJunct = entry.getKey();
				} else if (pathEstimate < minCost){
					minCost = pathEstimate;
					currentJunct = entry.getKey();
				}
			}
			
			if ((currentJunct.getX() == goal.getX()) && (currentJunct.getY() == goal.getY())) {
				return makePath(start, goal);
			}
			
			frontier.remove(currentJunct);
			searched.add(currentJunct);		
			
			for (Junction neighbour : currentJunct.getNeighbours()) {
				
							
				if ((neighbour == null) || (searched.contains(neighbour)))
					continue;
				
				
				if (!frontier.containsKey(neighbour)){
					// For safety
					frontier.put(neighbour, movesFromStart + 1);
				} else if ((movesFromStart + 1) >= frontier.get(neighbour))
					continue;
				
				frontier.remove(neighbour);
				frontier.put(neighbour, movesFromStart + 1);
				
				cameFrom.put(neighbour, currentJunct);
			}
		}
		
		return null;
	}
	
	public ArrayList<Direction> makePath(Junction start, Junction current) {
		
		ArrayList<Junction> revPath = new ArrayList<Junction>();
		
		
		if((start.getX() != current.getX()) || (start.getY() != current.getY())) //If start node != goal node
		{revPath.add(current);}
		
		while ((start.getX() != current.getX()) || (start.getY() != current.getY())) {
			revPath.add(cameFrom.get(current));
			current = cameFrom.get(current);
		}
		
		Collections.reverse(revPath);
		ArrayList<Direction> moveList = new ArrayList<Direction>();
		
		for (int i = 0; i < revPath.size() - 1; i++){
			
			Junction first = revPath.get(i);
			Junction second = revPath.get(i + 1);
			
			if (second.getX() > first.getX())
			{
				moveList.add(Direction.X_POS);
			}
			
			if (second.getX() < first.getX())
			{
				moveList.add(Direction.X_NEG);
			}
			
			if (second.getY() > first.getY())
			{
				moveList.add(Direction.Y_POS);
			}
			
			if (second.getY() < first.getY())
			{
				moveList.add(Direction.Y_NEG);
			}
		}
		
		return moveList;
	}
	
	//Heuristic calculator in Manhattan distance
	public int getHeuristic(Junction current, Junction goal)
	{
		return (Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY()));
	}
}
