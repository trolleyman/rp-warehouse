package warehouse.shared;

import java.util.ArrayList;

import warehouse.gui.State;
import warehouse.gui.TestStates;
import warehouse.job.Job;
import warehouse.job.JobList;

/**
 * The main interface for the whole project. Get the server using Server::get().
 * 
 * This holds the current state of the warehouse. The server's state can be updated by different
 * modules, and listeners will be notified of the updated state.
 * 
 * Later this will also hold the current jobs left to process, the jobs being processed, and the
 * jobs that have been completed.
 */
public class Server {
	private volatile static Object serverInitLock = new Object();
	private volatile static Server server = null;
	
	public static Server get() {
		synchronized (serverInitLock) {
			if (server == null) {
				server = new Server();
			}
			return server;
		}
	}
	
	private ArrayList<RobotListener> robotListeners;
	private State currentState;
	
	private ArrayList<JobListener> jobListeners;
	private JobList jobList;
	
	private Server() {
		robotListeners = new ArrayList<>();
		currentState = TestStates.TEST_STATE1;
		
		jobListeners = new ArrayList<>();
		jobList = new JobList("");
	}
	
	/**
	 * Adds a robot listener to the server that will be notified when a robot has been updated.
	 */
	public synchronized void addRobotListener(RobotListener _l) {
		robotListeners.add(_l);
	}
	
	/**
	 * Updated a robot {@code _r} with new information.
	 */
	public synchronized void updateRobot(Robot _r) {
		currentState.updateRobot(_r);
		for (RobotListener l : robotListeners) {
			l.robotChanged(_r);
		}
	}
	
	/**
	 * Adds a job listener to the server that will be notified when a job has been updated.
	 */
	public synchronized void addJobListener(JobListener _l) {
		jobListeners.add(_l);
	}
	
	/**
	 * Updated a job {@code _j} with new information.
	 */
	public synchronized void updateJob(Job _j) {
		for (Job job : jobList.getJobList()) {
			if (job.getId() == _j.getId()) {
				job = _j;
				break;
			}
		}
		for (JobListener j : jobListeners) {
			j.jobUpdated(_j);
		}
	}
	
	/**
	 * Gets the job list containing jobs that are completed, not completed, and in progress.
	 */
	public synchronized JobList getJobList() {
		return jobList;
	}
	
	/**
	 * Gets the current state of the system.
	 */
	public synchronized State getCurrentState() {
		return currentState;
	}
	
	/**
	 * Perform cleanup operations and then call {@code System.exit(0)}
	 * e.g. telling all robots to shut down.
	 */
	public void close() {
		synchronized (serverInitLock) {
			synchronized (this) {
				server = null;
			}
		}
	}
}
