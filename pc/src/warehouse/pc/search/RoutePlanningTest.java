package warehouse.pc.search;

import java.util.ArrayList;
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
	static Robot robotA;
	
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
	static Item yazoo;
	static Item lego;
	static Item crackers;
	
	static ArrayList<Junction> bases;
	static HashMap<Robot, LinkedList<Job>> map;
	
	public static void main(String[] args) {
		
		
		robot1 = new Robot("george", "george", 0.0, 0.0, 0.0);
		robot2 = new Robot("jason", "jason", 5.0, 0.0, 0.0);
		
		robotA = new Robot("miketheliar", "miketheliar", 4.0, 2.0, 180.0);
		
		
		
		
		tm1 = TestMaps.TEST_MAP1;
		tm2 = TestMaps.TEST_MAP2;
		tm3 = TestMaps.TEST_MAP3;
		tm4 = TestMaps.TEST_MAP4;
		
		heavy = new Item("heavy", 10, 20f, 4, 0);
		medium = new Item("medium", 10, 10f, 2, 0);
		light = new Item("light", 10, 5f, 3, 0);
		
		yazoo = new Item("yazoo", 50, 25f, 6, 2);
		lego = new Item("lego", 20, 10f, 1, 1);
		crackers = new Item("crackers", 1, 5f, 4, 0);
		
		map = new HashMap<Robot, LinkedList<Job>>();
		bases = new ArrayList<Junction>();
		
		bases.add(tm2.getJunction(0, 0));
		bases.add(tm2.getJunction(6, 0));
		
		tpl = new RoutePlanner(tm1, 60f, map, bases);

		tp3 = new RoutePlanner(tm3, 60f, map, bases);
		tp4 = new RoutePlanner(tm4, 60f, map, bases);
		
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
		
		jobA.add(new Job(0, listA, 140, 0));
		
		listB.add(new ItemQuantity(crackers, 10));
		listB.add(new ItemQuantity(yazoo, 1));
		
		jobA.add(new Job(1, listB, 75, 0));
		
		
		
		map.put(robotA, jobA);
		
		
		tp2 = new RoutePlanner(tm2, 60f, map, bases);
		tp2.computeCommands();
		
		
		LinkedList<Command> bearings = tp2.getCommands(robotA).getCommands();
		
		System.out.println(bearings);
		
	}
	
}
