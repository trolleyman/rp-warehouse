package warehouse.pc.shared;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * A queue of Bearings for the robot to follow
 * @author George
 *
 */

public class CommandQueue{

	private BlockingQueue<Bearing> commands;
	
	/**
	 * Create a new CommandQueue
	 */
	
	public CommandQueue(){
		
	}
	
	/**
	 * Add a single command to the queue
	 * @param bearing the bearing
	 */
	
	public void addCommand(Bearing bearing){
		commands.add(bearing);
	}
	
	/**
	 * Add a list of commands to the queue
	 * @param bearing the bearing
	 */
	
	public void addCommandList(List<Bearing> bearing){
		
		for(int i = 0; i < bearing.size(); i++){
			addCommand(bearing.get(i));
		}
	}
	
	/**
	 * Get the next command
	 * @return the next command
	 */
	
	public Bearing getNextCommand(){
		
		try {
			return commands.take();
		} catch (InterruptedException e) {}
		
		return null;
	}
	
}
