package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

	Command r = Command.RIGHT;
	Command l = Command.LEFT;
	Command f = Command.FORWARD;
	Command b = Command.BACKWARD;
	Command d = Command.DROP;
	Command w = Command.WAIT;
	Command p = Command.PICK;

	RoutePlanner plannerA;

	Item yazoo;
	Item lego;
	Item crackers;

	ArrayList<Junction> bases;
	HashMap<Robot, LinkedList<Job>> map;
	
	LinkedList<Command> bearingsA;
	LinkedList<Command> bearingsB;

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
		System.out.println(bearings);
		assertTrue(bearings.equals(Arrays.asList(f, l, f, l, p, b, f, d, b, l, f, f, f, f, p, l, r, d, r, r, f, f, f, f, f, l, p, b, f, r, f, p, b, f, d)));

		//locationTest(bearings, bearings1, robot1, robot2, Direction.Y_POS, Direction.Y_POS);

	}

	private void locationTest(List<Command> bearings1, List<Command> bearings2, Robot robot1, Robot robot2,
			Direction dir1, Direction dir2) {

		for (int i = 0; i < bearings1.size(); i++) {

			switch (bearings1.get(i)) {

			case FORWARD:
				switch (dir1) {
				case Y_POS:
					robot1.setY(robot1.getY() + 1);
					break;
				case Y_NEG:
					robot1.setY(robot1.getY() - 1);
					break;
				case X_POS:
					robot1.setX(robot1.getX() + 1);
					break;
				case X_NEG:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;

			case LEFT:

				switch (dir1) {
				case X_POS:
					robot1.setY(robot1.getY() + 1);
					break;
				case X_NEG:
					robot1.setY(robot1.getY() - 1);
					break;
				case Y_POS:
					robot1.setX(robot1.getX() + 1);
					break;
				case Y_NEG:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;
			case RIGHT:
				switch (dir1) {
				case X_NEG:
					robot1.setY(robot1.getY() + 1);
					break;
				case X_POS:
					robot1.setY(robot1.getY() - 1);
					break;
				case Y_NEG:
					robot1.setX(robot1.getX() + 1);
					break;
				case Y_POS:
					robot1.setX(robot1.getX() - 1);
					break;

				}

				break;
			case BACKWARD:
				switch (dir1) {
				case Y_NEG:
					robot1.setY(robot1.getY() + 1);
					break;
				case Y_POS:
					robot1.setY(robot1.getY() - 1);
					break;
				case X_NEG:
					robot1.setX(robot1.getX() + 1);
					break;
				case X_POS:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;
			default:
				break;

			}

			switch (bearings2.get(i)) {

			case FORWARD:
				switch (dir1) {
				case Y_POS:
					robot1.setY(robot1.getY() + 1);
					break;
				case Y_NEG:
					robot1.setY(robot1.getY() - 1);
					break;
				case X_POS:
					robot1.setX(robot1.getX() + 1);
					break;
				case X_NEG:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;

			case LEFT:

				switch (dir1) {
				case X_POS:
					robot1.setY(robot1.getY() + 1);
					break;
				case X_NEG:
					robot1.setY(robot1.getY() - 1);
					break;
				case Y_POS:
					robot1.setX(robot1.getX() + 1);
					break;
				case Y_NEG:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;
			case RIGHT:
				switch (dir1) {
				case X_NEG:
					robot1.setY(robot1.getY() + 1);
					break;
				case X_POS:
					robot1.setY(robot1.getY() - 1);
					break;
				case Y_NEG:
					robot1.setX(robot1.getX() + 1);
					break;
				case Y_POS:
					robot1.setX(robot1.getX() - 1);
					break;

				}

				break;
			case BACKWARD:
				switch (dir1) {
				case Y_NEG:
					robot1.setY(robot1.getY() + 1);
					break;
				case Y_POS:
					robot1.setY(robot1.getY() - 1);
					break;
				case X_NEG:
					robot1.setX(robot1.getX() + 1);
					break;
				case X_POS:
					robot1.setX(robot1.getX() - 1);
					break;
				}

				break;
			default:
				break;

			}

			assertTrue(!(robot1.getX() == robot2.getX() && robot1.getY() == robot2.getY()));

		}

	}

}
