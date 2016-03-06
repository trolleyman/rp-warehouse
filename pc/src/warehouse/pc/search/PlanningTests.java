package warehouse.pc.search;

import static org.junit.Assert.*;

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
import warehouse.pc.shared.Bearing;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;
import warehouse.shared.robot.Robot;

/**
 * Tests to check the planning aspect of the route finding:
 * 
 * Handling multiple robots
 * Returning to the base if the job would exceed the maximum weight
 * Returns to a base when the jobs are finished
 * Robots don't collide
 * 
 * @author George Kaye
 *
 */


public class PlanningTests {

	Map tm1;
	Map tm2;
	Map tm3;
	Map tm4;
	
	Robot robot1;
	Robot robot2;
	Robot robot3;
	
	Direction xp = Direction.X_POS;
	Direction xn = Direction.X_NEG;
	Direction yp = Direction.Y_POS;
	Direction yn = Direction.Y_NEG;
	
	Bearing r = Bearing.RIGHT;
	Bearing l = Bearing.LEFT;
	Bearing f = Bearing.FORWARD;
	Bearing b = Bearing.BACKWARD;
	
	RoutePlanner tpl;
	RoutePlanner tp2;
	RoutePlanner tp3;
	RoutePlanner tp4;
	
	Item heavy;
	Item medium;
	Item light;
	
	ArrayList<Junction> bases;
	HashMap<Robot, LinkedList<Job>> map;
	
	@Before
	public void setUp() throws Exception {
		
		robot1 = new Robot("george", "george", 0.0, 0.0, 0.0);
		robot2 = new Robot("jason", "jason", 5.0, 0.0, 0.0);
		robot3 = new Robot("alex", "alex", 3.0, 0.0, 0.0);
		
		
		tm1 = TestMaps.TEST_MAP1;
		tm2 = TestMaps.TEST_MAP2;
		tm3 = TestMaps.TEST_MAP3;
		tm4 = TestMaps.TEST_MAP4;
		
		heavy = new Item("heavy", 10, 20f, 1, 0);
		medium = new Item("medium", 10, 10f, 2, 0);
		light = new Item("light", 10, 5f, 3, 0);
		
		map = new HashMap<Robot, LinkedList<Job>>();
		
		bases = new ArrayList<>();
		bases.add(tm2.getJunction(0, 0));
		bases.add(tm2.getJunction(4, 0));
		
		tpl = new RoutePlanner(tm1, 60f, map, bases);

		tp3 = new RoutePlanner(tm3, 60f, map, bases);
		tp4 = new RoutePlanner(tm4, 60f, map, bases);
		
	}

	@After
	public void tearDown() throws Exception {
	}

/*   +---+---+---+---+---+---+
	 |   |   |   |   |   |   |
	 +---+---+---+---+---+---+
	 |       |       |       |
	 +       +       +       +
	 |       |       |      |
	 +       +       +       +
	 |       |       |       |
	 +       +       +       +
	 |       |       |       |
	 +---+---+---+---+---+---+
	 |   |   |   |   |   |   |
	 +---+---+---+---+---+---+*/
	
	
	// robot is currently always facing NORTH
	
	@Test
	public void test() {

		
		LinkedList<Job> job1 = new LinkedList<>();
		LinkedList<Job> job2 = new LinkedList<>();
		LinkedList<Job> job3 = new LinkedList<>();
		LinkedList<Job> job4 = new LinkedList<>();
		ArrayList<ItemQuantity> list = new ArrayList<>();
		ArrayList<ItemQuantity> list1 = new ArrayList<>();
		ArrayList<ItemQuantity> list2 = new ArrayList<>();
		ArrayList<ItemQuantity> list3 = new ArrayList<>();
		ArrayList<ItemQuantity> list4 = new ArrayList<>();
		list.add(new ItemQuantity(heavy, 3));
		list1.add(new ItemQuantity(light, 3));
		list1.add(new ItemQuantity(medium, 1));
		list1.add(new ItemQuantity(medium, 5));
		list2.add(new ItemQuantity(light, 10));
		list2.add(new ItemQuantity(medium, 1));
		list2.add(new ItemQuantity(heavy, 2));
		list2.add(new ItemQuantity(light, 1));
		list3.add(new ItemQuantity(light, 2));
		list3.add(new ItemQuantity(heavy, 1));
		list4.add(new ItemQuantity(heavy, 1));
		list4.add(new ItemQuantity(medium, 1));
		job1.add(new Job(0, list, 60, 0));
		job1.add(new Job(1, list1, 65, 0));
		job2.add(new Job(2, list2, 100, 0));
		job3.add(new Job(3, list3, 40, 0));
		job3.add(new Job(4, list4, 20, 0));
		
		map.put(robot1, job1);
		map.put(robot2, job2);
		map.put(robot3, job3);
		
		tp2 = new RoutePlanner(tm2, 60f, map, bases);
		tp2.computeCommands();
		LinkedList<Bearing> bearings = tp2.getCommands(robot1).getCommands();
		LinkedList<Bearing> bearings1 = tp2.getCommands(robot2).getCommands();
		LinkedList<Bearing> bearings2 = tp2.getCommands(robot3).getCommands();
		
		System.out.println(bearings);
		System.out.println(bearings1);
		System.out.println(bearings2);
		
		assertTrue(bearings.equals(Arrays.asList(r, b, b, f, f, b, f, f, b, f, b, f)));
		assertTrue(bearings1.equals(Arrays.asList(l, f, f, f, f, b, f, f, f)));
		assertTrue(bearings2.equals(Arrays.asList(l, f, f, b, f, f)));
		
		for(int i = 0; i < bearings1.size(); i++){
		
			
		
		}
		
		
		
	}

}
