package warehouse.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WarehouseMap {

	private int[][] map;
	private int width;
	private int height;
	private List<Integer> directions;
	private int pathCost;
	private int north;
	private int east;
	private int south;
	private int west;
	protected static final int FORWARDS = 0;
	protected static final int RIGHT = 1;
	protected static final int BACKWARDS = 2;
	protected static final int LEFT = 3;
	protected static final int NORTH = 0;
	protected static final int EAST = 1;
	protected static final int SOUTH = 2;
	protected static final int WEST = 3;
	private List<Node> frontier;

	public WarehouseMap(rp.robotics.mapping.GridMap gridMap) {

		this.width = gridMap.getXSize();
		this.height = gridMap.getYSize();
		this.directions = new LinkedList<Integer>();
		this.pathCost = 0;
		map = new int[height][width];

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {
				if (gridMap.isObstructed(i, j)) {
					map[i][j] = 1;
				} else {
					map[i][j] = 0;
				}
			}
		}

	}

	public int[][] getMap(){
		return map;
	}
	
	public void printMap() {

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {
				if (j == width - 1) {
					System.out.println(map[i][j]);
				} else {
					System.out.print(map[i][j]);
				}
			}
		}
	}

	/**
	 * Initialise a new arrayList with the possible directions the robot can move
	 * @param _x the current x coordinate of the robot
	 * @param _y the current y coordinate of the robot
	 * @return the arrayList of choices
	 */
	
	private ArrayList<Integer> fillChoices(int _x, int _y, int _facing) {

		ArrayList<Integer> choices = new ArrayList<Integer>();

		switch(_facing){
		case NORTH:
			north = FORWARDS;
			east = RIGHT;
			south = BACKWARDS;
			west = LEFT;
			break;
		case EAST:
			north = LEFT;
			east = FORWARDS;
			south = RIGHT;
			west = BACKWARDS;
			break;
		case SOUTH:
			north = BACKWARDS;
			east = LEFT;
			south = FORWARDS;
			west = RIGHT;
			break;
		case WEST:
			north = RIGHT;
			east = BACKWARDS;
			south = LEFT;
			west = FORWARDS;
			break;
		}		
		
		
		for (int i = 0; i < 3; i++) {
			choices.add(i);
		}
		
		if(_x == 0){
			choices.remove(west);
		}
		if(_y == 0){
			choices.remove(north);
		}
		
		if(_x == width - 1){
			choices.remove(east);
		}
		
		if(_y == height - 1){
			choices.remove(south);
		}
		
		return choices;

	}

	/**
	 * Find the shortest path between two points (using A*)
	 * @param _startX the start x coordinate of the robot
	 * @param _startY the start y coordinate of the robot
	 * @param _goalX the goal x coordinate
	 * @param _goalY the goal y coordinate
	 * @param _facing the direction the robot is facing
	 * @return the queue 
		
		of directions
	 */
	
	public List<Integer> aStar(int _startX, int _startY, int _goalX, int _goalY, int _facing, boolean begin){
		
		if(begin){
			this.frontier = new LinkedList<Node>();
		}
		
		Node current = new Node(_startX, _startY, null, null, 0, this);
		Node goal = new Node(_goalX, _goalY, null, null, 0, this);
		
		if(_startX == _goalX && _startY == _goalY){
			return directions;
		}
		
		ArrayList<Integer> choices = fillChoices(_startX, _startY, _facing);
		
		for(int i = 0; i < choices.size(); i++){
			
			Node node = null;
			
			switch(choices.get(i)){
			case FORWARDS:
				node = new Node(_startX, _startY - 1, current, goal, pathCost, this);
				break;
			case RIGHT:
				node = new Node(_startX + 1, _startY, current, goal, pathCost, this);
				break;
			case BACKWARDS:
				node = new Node(_startX, _startY + 1, current, goal, pathCost, this);
				break;
			case LEFT:
				node = new Node(_startX - 1, _startY, current, goal, pathCost, this);
				break;
	
			}
			
			frontier.add(node);
		}

		Collections.sort(frontier);
		System.out.println(frontier);
		
		
		
		
		return null;
		
	}

}
