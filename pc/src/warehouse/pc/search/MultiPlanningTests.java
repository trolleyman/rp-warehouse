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

public class MultiPlanningTests {

	static Map mapA;

	static Robot robotA;

	static Direction xp = Direction.X_POS;
	static Direction xn = Direction.X_NEG;
	static Direction yp = Direction.Y_POS;
	static Direction yn = Direction.Y_NEG;

	static Command r = Command.RIGHT;
	static Command l = Command.LEFT;
	static Command f = Command.FORWARD;
	static Command b = Command.BACKWARD;

	static MultiRoutePlanner plannerA;

	static Item yazoo;
	static Item lego;
	static Item crackers;

	static ArrayList<Junction> bases;
	static HashMap<Robot, LinkedList<Job>> map;

	public static void main(String[] args) {

		robotA = new Robot("george", "george", 0, 0, 0);
		
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

		jobA.add(new Job(0, listA, 140, 0));

		listB.add(new ItemQuantity(crackers, 10));
		listB.add(new ItemQuantity(yazoo, 1));

		jobA.add(new Job(1, listB, 75, 0));

		map.put(robotA, jobA);

		plannerA = new MultiRoutePlanner(mapA, 60f, map, bases, 10);


	}

}