package warehouse.pc.job;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.pc.shared.MainInterface;

public class JobSelectorTest {

	JobSelector selector;
	ArrayList<Job> jobList;
	
	@Before
	public void setUp() throws Exception {
		selector = new JobSelector("locations.csv", "items.csv", "jobs.csv", "drops.csv", MainInterface.get().getMap());
		jobList = selector.getList();
	}

	@After
	public void tearDown() throws Exception {
		selector = null;
		jobList = null;
	}

	@Test
	public void test() {
		Job firstJob;
		
		//Test that getJob() removes job from top of list
		firstJob = jobList.get(0);
		assertTrue(selector.getJob(0, 0, 50).get() == firstJob);
		
		//Test that job is removed once got.
		assertFalse(jobList.get(0) == firstJob);
	}

}
