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

public class RobotManager implements Runnable, RobotListener {
	private HashMap<Robot, ArrayDeque<Job>> robotJobs = new HashMap<>();
	private HashMap<Robot, CommandQueue> robotCommands = new HashMap<>();
	private HashMap<Robot, LinkedBlockingQueue<String>> robotMessages = new HashMap<>();
	private volatile MainInterface mi;
	
	private volatile ArrayList<Robot> robotsToAdd = new ArrayList<>();
	private volatile ArrayList<Robot> robotsToRemove = new ArrayList<>();
	
	private volatile boolean running;
	private volatile boolean nextStepRecalculate;
	private volatile boolean paused;
	
	public RobotManager() {
		
	}
	
	@Override
	public void run() {
		mi = MainInterface.get();
		
		doRecalculate();
		nextStepRecalculate = false;
		running = true;
		paused = true;
		
		while (running) {
			boolean sleep = false;
			if (!robotJobs.isEmpty() && !paused) {
				System.out.println("Robot Manager: Stepping system.");
			} else {
				//System.out.println("Robot Manager: No robots to manage.");
			}
			
			if (!paused)
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
				
				// TODO: If any robot doesn't have a command, recalculate.
			}
			
			try {
				if (sleep)
					Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
			
			if (nextStepRecalculate) {
				if (!robotCommands.isEmpty())
					System.out.println("Robot Manager: Recalculating Paths...");
				doRecalculate();
			}
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
				robotEmptyCommandJobs.put(e.getKey().clone(), new LinkedList<>(robotJobs.get(e.getKey())));
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
	 * i.e. Move robots and wait for their reply. TODO: Sort out localisation stuff as well.
	 */
	private void step() {
		// TODO: Update the position of the robots.
		//HashMap<Robot, Direction> newRobotDirections = new HashMap<>();

		ArrayList<Robot> readyRobots = new ArrayList<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			CommandQueue q = e.getValue();
			Command com = q.getCommands().peekFirst();
			if (com == null) {
				com = Command.WAIT;
			} else {
				q.getCommands().pop();
				readyRobots.add(e.getKey());
			}
			try {
				System.out.println("Sending to " + e.getKey().getIdentity() + ": " + com);
				mi.getServer().sendCommand(e.getKey(), com);
				new RobotUpdater(e.getKey(), com).start();
			} catch (IOException ex) {
				System.out.println(e.getKey().getIdentity() + " disconnected.");
				mi.removeRobot(e.getKey());
				continue;
			}
		}
		
		if (!readyRobots.isEmpty())
			System.out.println("Robot Manager: Waiting for robots to reply 'ready'...");
		
		for (Robot robot : readyRobots) {
			try {
				mi.getServer().waitForReady(robot.getName());
			} catch (IOException ex) {
				System.out.println(robot.getIdentity() + " disconnected.");
				mi.removeRobot(robot);
				continue;
			}
		}
	}
	
	/**
	 * Gets the commands for a robot.
	 * @param robot
	 */
	public CommandQueue getCommands(Robot robot) {
		return robotCommands.get(robot);
	}
	
	public void pause() {
		System.out.println("Robot Manager: Pausing.");
		paused = true;
	}
	
	public void resume() {
		System.out.println("Robot Manager: Resuming.");
		recalculate();
		paused = false;
	}
	
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
			System.out.println("Robot Manager: " + _r.getIdentity() + " added.");
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
