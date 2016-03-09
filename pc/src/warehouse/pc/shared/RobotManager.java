package warehouse.pc.shared;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import warehouse.pc.job.Job;
import warehouse.pc.job.JobSelector;
import warehouse.pc.search.RoutePlanner;
import warehouse.shared.Command;

public class RobotManager implements IRobotManager, RobotListener {
	HashMap<Robot, ArrayDeque<Job>> robotJobs = new HashMap<>();
	HashMap<Robot, CommandQueue> robotCommands = new HashMap<>();
	HashMap<Robot, LinkedBlockingQueue<String>> robotMessages = new HashMap<>();
	MainInterface mi;
	
	ArrayList<Robot> robotsToAdd = new ArrayList<>();
	ArrayList<Robot> robotsToRemove = new ArrayList<>();
	
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
			boolean sleep = false;
			if (!robotJobs.isEmpty()) {
				System.out.println("Robot Manager: Stepping system.");
			} else {
				//System.out.println("Robot Manager: No robots to manage.");
			}
			
			step();
			synchronized (this) {
				for (Robot robot : robotsToAdd) {
					robotJobs.put(robot, new ArrayDeque<>());
					robotCommands.put(robot, new CommandQueue());
					robotMessages.put(robot, new LinkedBlockingQueue<>());
					nextStepRecalculate = true;
				}
				robotsToAdd.clear();
				for (Robot robot : robotsToRemove) {
					robotJobs.remove(robot);
					robotCommands.remove(robot);
					robotMessages.remove(robot);
					nextStepRecalculate = true;
				}
				robotsToRemove.clear();
				
				if (robotCommands.isEmpty())
					sleep = true;
				
				// If any robot doesn't have a job, recalculate.
				for (Entry<Robot, ArrayDeque<Job>> e : robotJobs.entrySet()) {
					if (e.getValue().isEmpty()) {
						nextStepRecalculate = true;
						break;
					}
				}
			}
			
			try {
				if (sleep)
					Thread.sleep(500);
			} catch (InterruptedException e) {
				
			}
			
			if (!robotCommands.isEmpty())
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
		
		if (robotJobs.isEmpty()) {
			return;
		}
		
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
		// Ignore robots that already have commands.
		HashMap<Robot, LinkedList<Job>> robotEmptyCommandJobs = new HashMap<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			if (e.getValue().getCommands().isEmpty()) {
				robotEmptyCommandJobs.put(e.getKey(), new LinkedList<>(robotJobs.get(e.getKey())));
			}
		}
		RoutePlanner planner = new RoutePlanner(mi.getMap(), Robot.MAX_WEIGHT, robotEmptyCommandJobs, mi.getDropList().getList());
		planner.computeCommands();
		for (Entry<Robot, CommandQueue> robotCommand : robotCommands.entrySet()) {
			CommandQueue commands = planner.getCommands(robotCommand.getKey());
			if (commands != null) {
				robotCommand.setValue(commands);
			}
		}
		nextStepRecalculate = false;
	}

	/**
	 * Run one step of the system.
	 * i.e. Move robots and wait for their reply. Sort out localisation stuff as well.
	 */
	private void step() {
		// TODO: Update the position of the robots.
		ArrayList<Robot> waitOnRobots = new ArrayList<>();
		//HashMap<Robot, Direction> newRobotDirections = new HashMap<>();
		
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			CommandQueue q = e.getValue();
			Command com = q.getCommands().peekFirst();
			if (com == null) {
				com = Command.WAIT;
			} else {
				q.getCommands().pop();
				if (com.replyReady())
					waitOnRobots.add(e.getKey());
			}
			try {
				mi.getServer().sendToRobot(e.getKey().getName(), com.toString());
			} catch (IOException ex) {
				System.out.println(e.getKey().getIdentity() + " disconnected.");
				mi.removeRobot(e.getKey());
				waitOnRobots.remove(e.getKey());
				continue;
			}
		}
		
		if (!robotCommands.isEmpty())
			System.out.println("Robot Manager: Waiting for robots to reply 'ready'...");
		
		for (Robot robot : waitOnRobots) {
			String msg = null;
			while (msg == null || !msg.equalsIgnoreCase("ready")) {
				try {
					msg = mi.getServer().listen(robot.getName());
				} catch (IOException ex) {
					System.out.println(robot.getIdentity() + " disconnected.");
					mi.removeRobot(robot);
					continue;
				}
			}
		}
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
		synchronized (this) {
			System.out.println(_r.getIdentity() + " added to Robot Manager queue.");
			robotsToAdd.add(_r);
			recalculate();
		}
	}
	
	@Override
	public void robotRemoved(Robot _r) {
		synchronized (this) {
			System.out.println(_r.getIdentity() + " removed from Robot Manager queue.");
			robotsToRemove.add(_r);
			recalculate();
		}
	}
}
