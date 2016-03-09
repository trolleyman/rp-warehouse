package warehouse.pc.shared;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import warehouse.pc.bluetooth.MessageListener;
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
				}
				robotsToAdd.clear();
				for (Robot robot : robotsToRemove) {
					robotJobs.remove(robot);
					robotCommands.remove(robot);
					robotMessages.remove(robot);
				}
				
				if (robotCommands.isEmpty())
					sleep = true;
			}
			
			try {
				if (sleep)
					Thread.sleep(500);
			} catch (InterruptedException e) {
				
			}
			
			// TODO: Call doRecalculate if there are robots without jobs.
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
		HashMap<String, LinkedBlockingDeque<String>> robotRecieved = new HashMap<>();
		//HashMap<Robot, Direction> newRobotDirections = new HashMap<>();
		
		MessageListener listener = new MessageListener() {
			@Override
			public void newMessage(String robotName, String message) {
				robotRecieved.get(robotName).add(message);
			}
		};
		mi.getServer().addListener(listener);
		
		for (Entry<Robot, CommandQueue> e : robotCommands.entrySet()) {
			CommandQueue q = e.getValue();
			Command com = q.getCommands().peekFirst();
			if (com == null) {
				com = Command.WAIT;
			} else {
				q.getCommands().pop();
				robotRecieved.put(e.getKey().getName(), new LinkedBlockingDeque<>());
			}
			mi.getServer().sendToRobot(e.getKey().getName(), com.toString());
		}
		
		if (!robotCommands.isEmpty())
			System.out.println("Robot Manager: Waiting for robots to reply 'ready'...");
		
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
