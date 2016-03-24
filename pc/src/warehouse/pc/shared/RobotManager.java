package warehouse.pc.shared;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import rp.util.Pair;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.job.JobSelector;
import warehouse.pc.search.CMultiRoutePlanner;
import warehouse.pc.search.CReserveTable;
import warehouse.pc.search.RoutePlanner;

public class RobotManager implements Runnable, RobotListener {
	// Holds the current job & other jobs in the queue
	private HashMap<Robot, ArrayDeque<Job>> robotJobs = new HashMap<>();
	// Holds how much of the job is left to complete. Null signifies no job
	private HashMap<Robot, Job> robotPartialJobs = new HashMap<>();
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
					robotPartialJobs.put(robot, null);
					robotCommands.put(robot, new CommandQueue());
					robotMessages.put(robot, new LinkedBlockingQueue<>());
					nextStepRecalculate = true;
				}
				robotsToAdd.clear();
				// Remove robots
				for (Robot robot : robotsToRemove) {
					robotJobs.remove(robot);
					robotPartialJobs.remove(robot);
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
					robotPartialJobs.put(r, null);
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
					LinkedList<Command> coms = e.getValue().getCommands();
					if (coms != null && coms.peekFirst() != null && coms.peekFirst().equals(new Command(CommandType.COMPLETE_JOB))) {
						coms.pollFirst();
						robotPartialJobs.put(e.getKey(), null);
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
				Optional<Job> ojob = js.getJob((int) robot.getX(), (int) robot.getY(), Robot.MAX_WEIGHT);
				if (ojob.isPresent()) {
					Job j = ojob.get();
					jobs.offer(j);
					robotPartialJobs.put(robot, new Job(j.getId(), j.getItems(), j.getTotalWeight(), j.getTotalReward()));
				}
			}
		}
		
		// Calculate commands
		// Ignore robots that already have commands, reserve those spaces.
		CReserveTable reserve = new CReserveTable();
		HashMap<Robot, LinkedList<Job>> robotEmptyCommandJobs = new HashMap<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			Robot r = e.getKey();
			if (e.getValue().getCommands().isEmpty()) {
				// Calculate the robots that have no commands
				LinkedList<Job> jobs = new LinkedList<>();
				jobs.add(robotPartialJobs.get(e.getKey()));
				robotEmptyCommandJobs.put(e.getKey().clone(), jobs);
			} else {
				// Reserve positions
				reserve.reservePositions(new Junction(r.getGridX(), r.getGridY()),
					e.getValue().getCommands(), 0);
			}
		}
		
		// Create the multi route planner with specified reserve table.
		CMultiRoutePlanner planner = new CMultiRoutePlanner(mi.getMap(),
			mi.getDropList().getList(),
			mi.getRouteFinder(),
			reserve);
		
		HashMap<Robot, LinkedList<Command>> newRobotCommands = planner.routeRobots(robotEmptyCommandJobs);
		for (Entry<Robot, CommandQueue> robotCommand : robotCommands.entrySet()) {
			Robot r = robotCommand.getKey();
			LinkedList<Command> newCommands = newRobotCommands.get(r);
			if (newCommands == null)
				continue;
			
			CommandQueue commands = new CommandQueue(newRobotCommands.get(r));
			if (commands != null) {
				robotCommand.setValue(commands);
				mi.updateRobot(r);
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
		
		// Get list of robots with where they are in the array
		// Holds where robots will be.
		ArrayList<Pair<Robot, Junction>> newPos = new ArrayList<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			Command com = e.getValue().getCommands().peekFirst();
			com = (com == null ? new Command(CommandType.WAIT) : com);
			com.setFrom(e.getKey().getGridX(), e.getKey().getGridY());
			newPos.add(Pair.makePair(e.getKey(), mi.getMap().getJunction(com.getX(), com.getY())));
		}
		// Check for collisions
		for (int i = 0; i < newPos.size(); i++) {
			for (int j = i + 1; j < newPos.size(); j++) {
				Pair<Robot, Junction> ith = newPos.get(i);
				Pair<Robot, Junction> jth = newPos.get(j);
				if (ith.getItem2().equals(jth.getItem2())) {
					// Collision - add wait command to second (least prioritised robot)
					// Don't do this for now - might deadlock the whole system.
					/*
					Robot second = newPos.get(j).getItem1();
					robotCommands.get(second).getCommands().addFirst(Command.WAIT);*/
					System.out.println("Collision between "
						+ ith.getItem1().getIdentity()
						+ " and "
						+ jth.getItem1().getIdentity() + ".");
				}
			}
		}
		
		// Send commands to robots
		HashMap<Robot, Command> readyRobots = new HashMap<>();
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			CommandQueue q = e.getValue();
			Robot r = e.getKey();
			Command com = q.getCommands().peekFirst();
			if (com == null) {
				com = new Command(CommandType.WAIT);
			} else {
				com.setFrom(r.getGridX(), r.getGridY());
				r.setGridX(com.getX());
				r.setGridY(com.getY());
				q.getCommands().pop();
				readyRobots.put(r, com);
			}
			try {
				System.out.println("Sending to " + e.getKey().getIdentity() + ": " + com);
				Job j = robotPartialJobs.get(r);
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
		
		for (Entry<Robot, Command> e : readyRobots.entrySet()) {
			Robot robot = e.getKey();
			Command com = e.getValue();
			
			if (com.getType().equals(CommandType.PICK)) {
				// Get item at x,y
				String itemName = mi.getLocationList().getItemNameAt(robot.getGridX(), robot.getGridY());
				System.out.println("Item at " + robot.getGridX() + ", " + robot.getGridY() + ":" + itemName);
				ArrayList<ItemQuantity> items = robotPartialJobs.get(robot).getItems();
				int found = -1;
				for (int i = 0; i < items.size(); i++) {
					if (items.get(i).getItem().getName().equals(itemName)) {
						found = i;
						break;
					}
				}
				if (found != -1) {
					// Remove completed item
					items.remove(found);
				}
			}
			
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
	 * Gets the job left.
	 */
	public Job getPartialJob(Robot _r) {
		return robotPartialJobs.get(_r);
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
			paused = false;
		}
	}
	
	/**
	 * Recalculate all commands allocated to robots
	 */
	public void recalculateAll() {
		recalculateAllRobots = true;
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
