package warehouse.pc.job;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.shared.robot.Robot;

public class JobHandlerTest {

	JobHandler handler;
	
	@Before
	public void setUp() throws Exception {
		handler = new JobHandler("locations.csv", "items.csv", "jobs.csv", "drops.csv");
	}

	@After
	public void tearDown() throws Exception {
		handler = null;
	}

	@Test
	public void test() {
		//Test that jobs are assigned to all robots.
		Robot r1 = new Robot("r1", "0", 0, 0, 0);
		Robot r2 = new Robot("r2", "1", 1, 1, 0);
		Robot r3 = new Robot("r3", "2", 2, 2, 0);
		Robot[] robots = {r1, r2, r3};
		
		HashMap<Robot, LinkedList<Job>> map = handler.createJobMap(robots);
		for (int i = 0; i < 3; i++) {
			LinkedList<Job> jobList = map.get(robots[i]);
			assertFalse(jobList.size() == 0);
		}
	}

}
