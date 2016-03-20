package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;
import warehouse.pc.shared.Robot;

/**
 * Tests to check the planning aspect of the route finding:
 * 
 * Handling multiple robots Returning to the base if the job would exceed the
 * maximum weight Returns to a base when the jobs are finished Robots don't
 * collide
 * 
 * @author George Kaye
 *
 */

public class PlanningTests {

	Map mapA;

	Robot robotA;
	Robot robotB;
	
	Direction xp = Direction.X_POS;
	Direction xn = Direction.X_NEG;
	Direction yp = Direction.Y_POS;
	Direction yn = Direction.Y_NEG;

	Command u = Command.Y_POS;
	Command d = Command.Y_NEG;
	Command l = Command.X_NEG;
	Command r = Command.X_POS;
	Command dp = Command.DROP;
	Command w = Command.WAIT;
	Command p = Command.PICK;
	Command cj = Command.COMPLETE_JOB;

	RoutePlanner plannerA;

	Item yazoo;
	Item lego;
	Item crackers;

	ArrayList<Junction> bases;
	HashMap<Robot, LinkedList<Job>> map;
	
	ArrayDeque<Command> bearingsA;
	ArrayDeque<Command> bearingsB;

	@Before
	public void setUp() throws Exception {

		robotA = new Robot("wazowski", "wazowski", 4.0, 2.0, 180.0);
		robotB = new Robot("watkeysdowie", "watkeysdowie", 4.0, 2.0, 180.0);
		
		mapA = TestMaps.TEST_MAP2;

		yazoo = new Item("yazoo", 50, 25f, 6, 2);
		lego = new Item("lego", 20, 10f, 1, 1);
		crackers = new Item("crackers", 1, 5f, 4, 0);

		map = new HashMap<Robot, LinkedList<Job>>();
		bases = new ArrayList<Junction>();

		bases.add(mapA.getJunction(0, 0));
		bases.add(mapA.getJunction(6, 0));

		LinkedList<Job> jobA = new LinkedList<>();
		LinkedList<Job> jobB = new LinkedList<>();
		LinkedList<Job> jobC = new LinkedList<>();

		ArrayList<ItemQuantity> listA = new ArrayList<>();
		ArrayList<ItemQuantity> listB = new ArrayList<>();
		ArrayList<ItemQuantity> listC = new ArrayList<>();

		listA.add(new ItemQuantity(yazoo, 2));
		listA.add(new ItemQuantity(lego, 4));
		listA.add(new ItemQuantity(yazoo, 1));
		listA.add(new ItemQuantity(crackers, 5));

		Job firstJob = new Job(0, listA, 140, 0);
		
		jobA.add(firstJob);
		jobB.add(firstJob);

		listB.add(new ItemQuantity(crackers, 10));
		listB.add(new ItemQuantity(yazoo, 1));

		jobB.add(new Job(1, listB, 75, 0));

		map.put(robotA, jobA);
		map.put(robotB, jobB);

		plannerA = new RoutePlanner(mapA, 60f, map, bases);
		plannerA.computeCommands();

		bearingsA = plannerA.getCommands(robotA).getCommands();
		bearingsB = plannerA.getCommands(robotB).getCommands();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(bearingsA.equals(Arrays.asList(d, r, r, u, p, d, d, dp, u, l, l, l, l, l, p, d, l, dp, u, r, r, r, r, r, r, u, p, d, d, l, l, p, r, r, dp, cj)));
		assertTrue(bearingsB.equals(Arrays.asList(d, r, r, u, p, d, d, dp, u, l, l, l, l, l, p, d, l, dp, u, r, r, r, r, r, r, u, p, d, d, l, l, p, r, r, dp, l, l, p, r, r, dp, u, u, p, d, d, dp, cj)));
	}
}
