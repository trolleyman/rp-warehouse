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
	
	/**
	 * Create a new CommandQueue
	 */
	
	public CommandQueue(){
		commands = new LinkedList<Command>();
	}
	
	/**
	 * Add a single command to the queue
	 * @param bearing the bearing
	 */
	
	public void addCommand(Command bearing){
		commands.add(bearing);
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
	
}
