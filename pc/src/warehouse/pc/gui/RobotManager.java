package warehouse.pc.gui;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

import warehouse.pc.job.Job;
import warehouse.pc.shared.IRobotManager;
import warehouse.pc.shared.Robot;

public class RobotManager implements IRobotManager {
	HashMap<Robot, LinkedBlockingDeque<Job>> robotJobs = new HashMap<>();
	
	boolean running;
	
	public RobotManager() {
		
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			System.out.println("Robot Manager: Recalculating Paths...");
			recalculate();
		}
	}

	@Override
	public void recalculate() {
		for (Entry<Robot, LinkedBlockingDeque<Job>> robot : robotJobs.entrySet()) {
			
		}
	}
	
	@Override
	public void robotChanged(Robot _r) {
		// Ignore.
	}

	@Override
	public void robotAdded(Robot _r) {
		robotJobs.put(_r, new LinkedBlockingDeque<>());
		recalculate();
	}

	@Override
	public void robotRemoved(Robot _r) {
		robotJobs.remove(_r);
		recalculate();
	}

}
