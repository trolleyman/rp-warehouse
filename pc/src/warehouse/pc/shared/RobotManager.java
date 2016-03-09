package warehouse.pc.shared;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import warehouse.pc.bluetooth.MessageListener;
import warehouse.pc.job.Job;
import warehouse.pc.job.JobHandler;
import warehouse.pc.job.JobSelector;
import warehouse.pc.search.RoutePlanner;

public class RobotManager implements IRobotManager, RobotListener {
	HashMap<Robot, ArrayDeque<Job>> robotJobs = new HashMap<>();
	HashMap<Robot, CommandQueue> robotCommands = new HashMap<>();
	HashMap<Robot, LinkedBlockingQueue<String>> robotMessages = new HashMap<>();
	MainInterface mi;
	
	boolean running;
	private boolean nextStepRecalculate;
	
	public RobotManager() {
		
	}
	
	@Override
	public void run() {
		mi = MainInterface.get();
		
		doRecalculate();
		nextStepRecalculate = false;
		running = true;
		while (running) {
			System.out.println("Robot Manager: Stepping system.");
			step();
			// TODO: Call doRecalculate if there are robots without jobs.
			System.out.println("Robot Manager: Recalculating Paths...");
			if (nextStepRecalculate)
				doRecalculate();
		}
	}
	
	/**
	 * Recalculate the jobs & commands for the system
	 */
	private void doRecalculate() {
		JobSelector js = mi.getJobSelector();
		
		// Calculate jobs
		for (Entry<Robot, ArrayDeque<Job>> e : robotJobs.entrySet()) {
			Robot robot = e.getKey();
			ArrayDeque<Job> jobs = e.getValue();
			if (jobs.size() == 0) {
				Optional<Job> job = js.getJob((int) robot.getX(), (int) robot.getY(), Robot.MAX_WEIGHT);
				if (job.isPresent()) {
					jobs.offer(job.get());
				}
			}
		}
		
		// Calculate commands
		// TODO: Sort out what happens to robots that already have commands. Probably just ignore them.
		RoutePlanner planner = new RoutePlanner(mi.getMap(), Robot.MAX_WEIGHT, robotJobs, mi.getDropList().getList());
		for (Entry<Robot, CommandQueue> robotCommand : robotCommands.entrySet()) {
			CommandQueue commands = planner.getCommands(robotCommand.getKey());
			if (commands != null) {
				robotCommand.setValue(commands);
			}
		}
	}

	/**
	 * Run one step of the system.
	 * i.e. Move robots and wait for their reply. Sort out localisation stuff as well.
	 */
	private void step() {
		// TODO: Update the position of the robots.
		HashMap<String, LinkedBlockingDeque<String>> robotRecieved = new HashMap<>();
		
		MessageListener listener = new MessageListener() {
			@Override
			public void newMessage(String robotName, String message) {
				robotRecieved.get(robotName).add(message);
			}
		};
		mi.getServer().addListener(listener);
		
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			robotRecieved.put(e.getKey().getName(), new LinkedBlockingDeque<>());
			CommandQueue q = e.getValue();
			Command com = q.getCommands().removeFirst();
			if (com == null) {
				com = Command.WAIT;
			}
			mi.getServer().sendToRobot(e.getKey().getName(), com.toString());
		}
		
		System.out.println("RobotManager: Waiting for robots to reply 'ready'...");
		for (Entry<String, LinkedBlockingDeque<String>> e : robotRecieved.entrySet()) {
			String msg = null;
			while (msg == null || !msg.equals("ready")) {
				try {
					msg = e.getValue().pollFirst(10, TimeUnit.SECONDS);
				} catch (InterruptedException ex) {
					
				}
			}
		}
		
		mi.getServer().removeListener(listener);
	}
	
	@Override
	public void recalculate() {
		nextStepRecalculate = true;
	}
	
	@Override
	public void robotChanged(Robot _r) {
		// Ignore.
	}
	
	@Override
	public void robotAdded(Robot _r) {
		// TODO: synchronize these with the step() thing - add into a seperate robotToAdd queue?
		robotJobs.put(_r, new ArrayDeque<>());
		robotCommands.put(_r, new CommandQueue());
		robotMessages.put(_r, new LinkedBlockingQueue<>());
		recalculate();
	}
	
	@Override
	public void robotRemoved(Robot _r) {
		// TODO: Be more graceful in removing a robot - wait for step maybe?
		robotJobs.remove(_r);
		robotCommands.remove(_r);
		robotMessages.remove(_r);
		recalculate();
	}
}
