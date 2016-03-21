package warehouse.pc.search;

import java.util.ArrayList;
import java.util.LinkedList;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;

public class RoutePackage {

	private ArrayList<Direction> directionList;
	private LinkedList<Command> commandList;
	private ArrayList<Junction> junctionList;
	
	public RoutePackage(){
		
	}
	
	public RoutePackage(ArrayList<Direction> directionList, LinkedList<Command> commandList, ArrayList<Junction> junctionList){
		
		this.directionList = directionList;
		this.commandList = commandList;
		this.junctionList = junctionList;
		
	}
	
	public ArrayList<Direction> getDirectionList(){
		return directionList;
	}

	public LinkedList<Command> getCommandList() {
		return commandList;
	}
	
	public ArrayList<Junction> getJunctionList(){
		return junctionList;
	}

	public void setDirectionList(ArrayList<Direction> directionList) {
		this.directionList = directionList;
	}

	public void setCommandList(LinkedList<Command> commandList) {
		this.commandList = commandList;
	}

	public void setJunctionList(ArrayList<Junction> junctionList) {
		this.junctionList = junctionList;
	}
	
	public String toString(){
		return directionList.toString();
	}
	
	
	
}
