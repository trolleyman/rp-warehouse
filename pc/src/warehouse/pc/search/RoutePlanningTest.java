package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;

public class RoutePlanningTest {

	static Map tm1;
	static Map tm2;
	static Map tm3;
	static Map tm4;
	
	static Robot robot1;
	static Robot robot2;
	static Robot robot3;
	
	static Direction xp = Direction.X_POS;
	static Direction xn = Direction.X_NEG;
	static Direction yp = Direction.Y_POS;
	static Direction yn = Direction.Y_NEG;
	
	static Command r = Command.RIGHT;
	static Command l = Command.LEFT;
	static Command f = Command.FORWARD;
	static Command b = Command.BACKWARD;
	
	static RoutePlanner tpl;
	static RoutePlanner tp2;
	static RoutePlanner tp3;
	static RoutePlanner tp4;
	
	static Item heavy;
	static Item medium;
	static Item light;
	
	static ArrayList<Junction> bases;
	static HashMap<Robot, LinkedList<Job>> map;
	
	public static void main(String[] args) {
		
		
		robot1 = new Robot("george", "george", 0.0, 0.0, 0.0);
		robot2 = new Robot("jason", "jason", 5.0, 0.0, 0.0);
		robot3 = new Robot("alex", "alex", 3.0, 0.0, 0.0);
		
		
		tm1 = TestMaps.TEST_MAP1;
		tm2 = TestMaps.TEST_MAP2;
		tm3 = TestMaps.TEST_MAP3;
		tm4 = TestMaps.TEST_MAP4;
		
		heavy = new Item("heavy", 10, 20f, 4, 0);
		medium = new Item("medium", 10, 10f, 2, 0);
		light = new Item("light", 10, 5f, 3, 0);
		
		map = new HashMap<Robot, LinkedList<Job>>();
		
		bases = new ArrayList<>();
		bases.add(tm2.getJunction(0, 0));
		bases.add(tm2.getJunction(4, 0));
		
		tpl = new RoutePlanner(tm1, 60f, map, bases);

		tp3 = new RoutePlanner(tm3, 60f, map, bases);
		tp4 = new RoutePlanner(tm4, 60f, map, bases);
		
		LinkedList<Job> job1 = new LinkedList<>();
		ArrayList<ItemQuantity> list = new ArrayList<>();
		ArrayList<ItemQuantity> list1 = new ArrayList<>();
		list.add(new ItemQuantity(heavy, 3));
		list1.add(new ItemQuantity(light, 3));
		list1.add(new ItemQuantity(medium, 1));
		list1.add(new ItemQuantity(medium, 5));
		job1.add(new Job(0, list, 60, 0));
		job1.add(new Job(1, list1, 65, 0));
		
		map.put(robot1, job1);
		
		tp2 = new RoutePlanner(tm2, 60f, map, bases);
		tp2.computeCommands();
		LinkedList<Command> bearings = tp2.getCommands(robot1).getCommands();
		
		System.out.println(bearings);
		
		/*assertTrue(bearings.equals(Arrays.asList(r, b, b, f, f, b, f, f, b, f, b, f)));
		assertTrue(bearings1.equals(Arrays.asList(l, f, f, f, f, b, f, f, f)));
		assertTrue(bearings2.equals(Arrays.asList(l, f, f, b, f, f)));*/
		
	}
	
}
