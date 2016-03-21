package warehouse.pc.shared;

import java.util.LinkedList;
import java.util.List;

/**
 * A queue of Bearings for the robot to follow
 * @author George
 *
 */

public class CommandQueue{

	private LinkedList<Command> commands;
	private LinkedList<Junction> junctions;
	
	/**
	 * Create a new CommandQueue
	 */
	
	public CommandQueue(){
		commands = new LinkedList<>();
		junctions = new LinkedList<>();
	}
	
	/**
	 * Add a single command to the queue
	 * @param bearing the bearing
	 */
	
	public void addCommand(Command bearing){
		commands.add(bearing);
	}
	
	/**
	 * Add a single junction to the queue
	 * @param junction the junction
	 */
	
	public void addJunction(Junction junction){
		junctions.add(junction);
	}
	
	/**
	 * Add a list of commands to the queue
	 * @param bearing the bearing
	 */
	
	public void addCommandList(List<Command> bearing){
		
		for(int i = 0; i < bearing.size(); i++){
			addCommand(bearing.get(i));
		}
	}
	
	/**
	 * Add a list of junctions to the queue
	 * @param junctions the junction
	 */
	
	public void addJunctionList(List<Junction> junctions){
		
		for(int i = 0; i < junctions.size(); i++){
			addJunction(junctions.get(i));
		}
	}
	
	/**
	 * Get the next command
	 * @return the next command
	 */
	
	public Command getNextCommand(){
		
		return commands.pop();
	}
	
	/**
	 * Get the whole list of commands
	 * @return the list of commands
	 */
	
	public LinkedList<Command> getCommands(){
		return commands;
	}
	
	/**
	 * Get the whole list of junctions
	 * @return the list of junctions
	 */
	
	public LinkedList<Junction> getJunctions(){
		return junctions;
	}
	
	public String toString(){
		return commands.toString();
	}
	
}
