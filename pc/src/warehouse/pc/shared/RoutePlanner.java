package warehouse.pc.shared;

import java.util.ArrayList;
import java.util.HashMap;

import warehouse.shared.robot.Robot;

/**
 * Class to create lists of bearings for individual robots to take VERY MUCH IN
 * PROGRESS - THERE ARE LOTS OF SQUIGGLY RED LINES EVERYWHERE
 *
 */

public class RoutePlanner {

	private HashMap<Robot, Jobs> pairedJobs;
	private HashMap<Robot, Commands> pairedCommands;
	private RouteFinder finder;
	private ArrayList<Bearing> directionList;

	public RoutePlanner(Robot _robot, Map _map) {
		finder = new RouteFinder(_map);
		directionList = new ArrayList<Bearing>();
	}

	/**
	 * Gets the commands object for a given robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the commands object
	 */

	public Commands getCommands(Robot _robot) {
		return pairedCommands.get(_robot);

	}

	/**
	 * Gets the hashmap of robots mapped to commands for a given robot
	 * 
	 * @param _robot
	 *            the robot
	 * @return the hashmap
	 */

	public HashMap<Robot, Commands> getPairedCommands(Robot _robot) {
		return this.pairedCommands;
	}

	/**
	 * No idea what this does
	 */

	public void parseCommands() {

	}

	/**
	 * Makes lists of commands for the robots
	 * @param _pairedjobs the hashmap of robots to jobs
	 */
	
	private void computeCommands(HashMap <Robot, Jobs> _pairedjobs){
		
		for(int i = 0; i < _pairedjobs.size(); i++){
			
		/*
		 * not sure how the robots are defined, hopefully 1,2,3 so this loop is easy
		 * if not maybe a list of robots?	
		 */
		ArrayList<Job> jobList = _pairedJobs.get(i); //not sure how the robots are defined, hopefully 1,2,3 so this loop is easy - if not maybe an arraylist of robots?
		
			for(int j = 0; j < jobList.size(); j++){
				
				Job job = jobList.get(j); // get the jth job from the job list
				
				for(int k = 0; k < job.size(); k++){
					
					Item item = job.get(k); //get the kth item from the job
				
					Junction start = robot.position; // robot's position will change between jobs, don't forget that
					Junction goal= item.position;     // this is probably legit
					Direction facing = robot.facing  // is this an attribute? it needs to be
				
					ArrayList<Bearing> list = finder.findRoute(start, goal, facing); // find the route 
					directionList.addAll(list); // add the directions to the list for this robot

					robot.setPosition(goal); // this might already be handled elsewhere?
				}
				
			}
		
		}
		
	}

}
