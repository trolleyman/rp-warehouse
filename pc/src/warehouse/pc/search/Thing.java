package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import warehouse.pc.job.Job;
import warehouse.pc.shared.Robot;

public class Thing {
	public RoutePackage routeRobot(Robot r, LinkedList<Job> j, ArrayList<Junction>[] reseveTable) {
		for each job {
			for each item in job {
				if item pushes me over limit {
					path to base
					set current position
				}
				calculate from current position to item
				set current position
				queue calculated commands
			}
			calculate from current position to base
			set current position to base
		}
		add Command.COMPLETE_JOB
		from now (commands.size()) til the end of time, reserve current position
	}
	
	
	// Calculates route and reserves positions
	public RoutePackage calculateCommands(Junction from, Junction to, reseveTable) {
		
	}
}
