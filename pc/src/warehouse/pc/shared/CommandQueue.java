package warehouse.pc.shared;

import java.util.LinkedList;
import java.util.List;

/**
 * A queue of Directions for the robot to follow
 * 
 * @author George
 *
 */

public class CommandQueue {

	private LinkedList<Command> commands;
	private LinkedList<Junction> junctions;

	/**
	 * Create a new CommandQueue
	 */

	public CommandQueue() {

		commands = new LinkedList<>();
		junctions = new LinkedList<>();

	}

	/**
	 * Add a single command to the queue
	 * 
	 * @param command
	 *            the command
	 */

	public void addCommand(Command command) {
		commands.add(command);
	}

	/**
	 * Add a single junction to the queue
	 * 
	 * @param junction
	 *            the junction
	 */

	public void addJunction(Junction junction) {
		junctions.add(junction);
	}

	/**
	 * Add a list of commands to the queue
	 * 
	 * @param list
	 *            the commands
	 */

	public void addCommandList(LinkedList<Command> list) {
		for (Command c : list) {
			addCommand(c);
		}
	}

	/**
	 * Add a list of junctions to the queue
	 * 
	 * @param junctions
	 *            the junction
	 */

	public void addJunctionList(List<Junction> junctions) {

		for (int i = 0; i < junctions.size(); i++) {
			addJunction(junctions.get(i));
		}
	}

	/**
	 * Get the next command
	 * 
	 * @return the next command
	 */

	public Command getNextCommand() {

		return commands.pollFirst();
	}

	/**
	 * Get the whole list of commands
	 * 
	 * @return the list of commands
	 */

	public LinkedList<Command> getCommands() {
		return commands;
	}

	/**
	 * Get the whole list of junctions
	 * 
	 * @return the list of junctions
	 */

	public LinkedList<Junction> getJunctions() {
		return junctions;
	}

	/**
	 * Set the last element to a different one
	 * 
	 * @param command
	 *            the command
	 */

	public void setLastCommand(Command command) {
		commands.set(commands.size() - 1, command);
	}

	public String toString() {
		return commands.toString();
	}

}
