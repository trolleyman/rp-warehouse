package warehouse.pc.shared;

import java.util.ArrayDeque;

/**
 * A queue of Bearings for the robot to follow
 * @author George
 *
 */

public class CommandQueue{

	private ArrayDeque<Command> commands;
	
	/**
	 * Create a new CommandQueue
	 */
	
	public CommandQueue(){
		commands = new ArrayDeque<Command>();
	}
	
	/**
	 * Add a single command to the queue
	 * @param command the command
	 */
	
	public void addCommand(Command command){
		commands.add(command);
	}
	
	/**
	 * Add a list of commands to the queue
	 * @param commands the commands
	 */
	
	public void addCommandList(ArrayDeque<Command> commands){
		for (Command c : commands) {
			addCommand(c);
		}
	}
	
	/**
	 * Get the next command
	 * @return the next command
	 */
	
	public Command getNextCommand(){
		
		return commands.pollFirst();
	}
	
	/**
	 * Get the whole list of commands
	 * @return the list of commands
	 */
	
	public ArrayDeque<Command> getCommands(){
		return commands;
	}
	
}
