package warehouse.pc.search;

import java.util.ArrayList;
import java.util.LinkedList;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;

public class RoutePackage {

	private ArrayList<Direction> directionList;
	private LinkedList<Command> commandList;
	private ArrayList<ArrayList<Junction>> reservedJunctions;
	private int robotPriority;
	
	public RoutePackage(ArrayList<Direction> directionList, LinkedList<Command> commandList, ArrayList<ArrayList<Junction>> reservedJunctions, int robotPriority){
		
		this.directionList = directionList;
		this.commandList = commandList;
		this.reservedJunctions = reservedJunctions;
		this.robotPriority = robotPriority;
		
	}
	
	public ArrayList<Direction> getDirections(){
		return directionList;
	}

	public LinkedList<Command> getCommandList() {
		return commandList;
	}

	public ArrayList<ArrayList<Junction>> getReservedJunctions() {
		return reservedJunctions;
	}

	public int getRobotPriority() {
		return robotPriority;
	}
	
	
	
}
