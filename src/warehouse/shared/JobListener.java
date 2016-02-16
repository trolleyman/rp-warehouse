package warehouse.shared;

import warehouse.job.Job;

/**
 * Defines an interface whose methods are called when a job's status is updated.
 */
public interface JobListener {
	public void jobUpdated(Job _job);
}
