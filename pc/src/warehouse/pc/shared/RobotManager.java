package warehouse.pc.shared;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.bluetooth.ServerSender;
import warehouse.pc.job.Job;

public class RobotManager implements IRobotManager {
	HashMap<Robot, LinkedBlockingDeque<Job>> robotJobs = new HashMap<>();
	HashMap<Robot, CommandQueue> robotCommands = new HashMap<>();
	HashMap<Robot, ServerSender> senders = new HashMap<>();
	
	boolean running;
	
	public RobotManager() {
		
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			System.out.println("Robot Manager: Stepping system.");
			step();
			System.out.println("Robot Manager: Recalculating Paths...");
			recalculate();
		}
	}
	
	@Override
	public void step() {
		for (Entry<Robot, ServerSender> sender : senders.entrySet()) {
			
		}
	}

	@Override
	public void recalculate() {
		
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
