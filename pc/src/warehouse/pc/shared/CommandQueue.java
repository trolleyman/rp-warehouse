package warehouse.pc.shared;

import java.util.ArrayDeque;

/**
 * A queue of Bearings for the robot to follow
 * @author George
 *
 */

public class CommandQueue{

<<<<<<< HEAD
	private LinkedList<Command> commands;
	private LinkedList<Junction> junctions;
=======
	private ArrayDeque<Command> commands;
>>>>>>> master
	
	/**
	 * Create a new CommandQueue
	 */
	
	public CommandQueue(){
<<<<<<< HEAD
		commands = new LinkedList<>();
		junctions = new LinkedList<>();
=======
		commands = new ArrayDeque<Command>();
>>>>>>> master
	}
	
	/**
	 * Add a single command to the queue
	 * @param command the command
	 */
	
	public void addCommand(Command command){
		commands.add(command);
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
	 * @param commands the commands
	 */
	
	public void addCommandList(ArrayDeque<Command> commands){
		for (Command c : commands) {
			addCommand(c);
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
		
		return commands.pollFirst();
	}
	
	/**
	 * Get the whole list of commands
	 * @return the list of commands
	 */
	
	public ArrayDeque<Command> getCommands(){
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
