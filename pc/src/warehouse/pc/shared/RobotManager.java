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
	private volatile ArrayList<Robot> robotJobsToCancel = new ArrayList<>();
	
	private volatile ArrayList<Job> completedJobs = new ArrayList<>();
	
	private volatile boolean running;
	private volatile boolean nextStepRecalculate;
	private volatile boolean recalculateAllRobots;
	private volatile boolean paused;
	
	public RobotManager() {
		
	}
	
	@Override
	public void run() {
		mi = MainInterface.get();
		
		doRecalculate();
		nextStepRecalculate = false;
		recalculateAllRobots = false;
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
				// Add robots
				for (Robot robot : robotsToAdd) {
					robotJobs.put(robot, new ArrayDeque<>());
					robotCommands.put(robot, new CommandQueue());
					robotMessages.put(robot, new LinkedBlockingQueue<>());
					nextStepRecalculate = true;
				}
				robotsToAdd.clear();
				// Remove robots
				for (Robot robot : robotsToRemove) {
					robotJobs.remove(robot);
					robotCommands.remove(robot);
					robotMessages.remove(robot);
					nextStepRecalculate = true;
				}
				robotsToRemove.clear();
				
				// If no robots, sleep for a bit
				if (robotCommands.isEmpty())
					sleep = true;
				
				// Remove robot jobs
				for (Robot r : robotJobsToCancel) {
					ArrayDeque<Job> jq = robotJobs.get(r);
					if (jq == null)
						continue;
					
					// Cancel every job in the queue
					for (Job j : jq) {
						mi.getJobList().getList().add(j);
						System.out.println("Robot Manager: Cancelled job: " + j);
					}
					jq.clear();
				}
				robotJobsToCancel.clear();
				
				// If any robot's next command is Command.COMPLETE_JOB, complete job & recalculate.
				for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
					ArrayDeque<Command> coms = e.getValue().getCommands();
					if (coms.peekFirst() != null && coms.peekFirst().equals(Command.COMPLETE_JOB)) {
						coms.pollFirst();
						ArrayDeque<Job> jobs = robotJobs.get(e.getKey());
						if (jobs != null && !jobs.isEmpty()) {
							// Complete first job in queue
							Job j = jobs.removeFirst();
							System.out.println("Robot Manager: Completed Job: " + j);
							completedJobs.add(j);
							nextStepRecalculate = true;
						}
					}
				}
				
				// If any robot doesn't have a job, recalculate.
				for (Entry<Robot, ArrayDeque<Job>> e : robotJobs.entrySet()) {
					if (e.getValue().isEmpty()) {
						nextStepRecalculate = true;
						break;
					}
				}
				
				// Remove all commands from all robots, and recalculate
				if (recalculateAllRobots) {
					for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
						e.getValue().getCommands().clear();
					}
					nextStepRecalculate = true;
					recalculateAllRobots = false;
				}
			}
			
			try {
				if (sleep)
					Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
			
			if (nextStepRecalculate) {
				if (!robotCommands.isEmpty())
					System.out.println("Robot Manager: Recalculating...");
				long t0 = System.currentTimeMillis();
				doRecalculate();
				long t1 = System.currentTimeMillis();
				long ms = t1 - t0;
				if (!robotCommands.isEmpty())
					System.out.println("Robot Manager: Done Recalculating. (" + ms + "ms)");
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
	 * i.e. Move robots and wait for their reply.
	 */
	private void step() {
		//HashMap<Robot, Direction> newRobotDirections = new HashMap<>();
		
		ArrayList<RobotUpdater> robotsToUpdate = new ArrayList<>();
		
		ArrayList<Robot> readyRobots = new ArrayList<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			CommandQueue q = e.getValue();
			Robot r = e.getKey();
			Command com = q.getCommands().peekFirst();
			if (com == null) {
				com = Command.WAIT;
			} else {
				com.setFrom(r.getGridX(), r.getGridY());
				r.setGridX(com.getX());
				r.setGridY(com.getY());
				q.getCommands().pop();
				readyRobots.add(r);
			}
			try {
				System.out.println("Sending to " + e.getKey().getIdentity() + ": " + com);
				Job j = robotJobs.getOrDefault(r, new ArrayDeque<>()).peekFirst();
				if (j != null)
					mi.getServer().sendToRobot(r.getName(), "Cancel Job:" + j.getId());
				mi.getServer().sendCommand(r, com);
				RobotUpdater ru = new RobotUpdater(r, com);
				robotsToUpdate.add(ru);
				ru.start();
			} catch (IOException ex) {
				System.out.println(r.getIdentity() + " disconnected.");
				mi.removeRobot(r);
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
		
		for (RobotUpdater ru : robotsToUpdate) {
			ru.end();
		}
	}
	
	/**
	 * Gets the commands for a robot.
	 */
	public CommandQueue getCommands(Robot robot) {
		return robotCommands.get(robot);
	}
	
	/**
	 * Gets the jobs assigned to a robot.
	 */
	public ArrayDeque<Job> getJobs(Robot _r) {
		return robotJobs.get(_r);
	}
	
	/**
	 * Pauses the RobotManager gracefully, i.e. it waits for all robots to reach the next junction, then it starts.
	 */
	public void pause() {
		synchronized (this) {
			System.out.println("Robot Manager: Pausing.");
			paused = true;
		}
	}
	
	/**
	 * Resumes the RobotManager.
	 */
	public void resume() {
		synchronized (this) {
			System.out.println("Robot Manager: Resuming.");
			// Recalculate all commands - we don't know if the robots have changed position
			recalculate();
			recalculateAllRobots = true;
			paused = false;
		}
	}
	
	/**
	 * Gets the jobs that have been completed by the RobotManager.
	 */
	public ArrayList<Job> getCompletedJobs() {
		synchronized (this) {
			return new ArrayList<>(completedJobs);
		}
	}
	
	/**
	 * Triggers the RobotManager to reallocate jobs to robots with no jobs.
	 */
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
	
	/**
	 * Cancels the jobs on a certain robot.
	 * @param robot the robot
	 */
	public void cancelJobs(Robot robot) {
		synchronized (this) {
			robotJobsToCancel.add(robot);
		}
	}
}
