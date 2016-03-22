package warehouse.pc.search;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;

public class MultiPlanningTests {

	static Map mapA;

	static Robot robotA;
	static Robot robotB;
	static Robot robotC;

	static Direction xp = Direction.X_POS;
	static Direction xn = Direction.X_NEG;
	static Direction yp = Direction.Y_POS;
	static Direction yn = Direction.Y_NEG;

//	static Command r = Command.RIGHT;
//	static Command l = Command.LEFT;
//	static Command f = Command.FORWARD;
//	static Command b = Command.BACKWARD;

	static MultiRoutePlanner plannerA;

	static Item yazoo;
	static Item lego;
	static Item crackers;

	static ArrayList<Junction> bases;

	static HashMap<Robot, LinkedList<Job>> map1;
	static HashMap<Robot, LinkedList<Job>> map2;
	static HashMap<Robot, LinkedList<Job>> map3;

	public static void main(String[] args) {

		robotA = new Robot("george", "george", 0, 3, 0);
		robotB = new Robot("jason", "jason", 0, 4, 0);
		robotC = new Robot("lenka", "lenka", 0, 5, 0);

		mapA = TestMaps.TEST_MAP4;

		yazoo = new Item("yazoo", 50, 25f, 3, 1);
		lego = new Item("lego", 20, 10f, 2, 1);
		crackers = new Item("crackers", 1, 5f, 0, 0);

		map1 = new HashMap<Robot, LinkedList<Job>>();

		bases = new ArrayList<Junction>();

		bases.add(mapA.getJunction(3, 7));
		bases.add(mapA.getJunction(5, 7));
		bases.add(mapA.getJunction(7, 7));

		LinkedList<Job> jobA = new LinkedList<>();
		LinkedList<Job> jobB = new LinkedList<>();
		LinkedList<Job> jobC = new LinkedList<>();

		ArrayList<ItemQuantity> listA = new ArrayList<>();
		ArrayList<ItemQuantity> listB = new ArrayList<>();
		ArrayList<ItemQuantity> listC = new ArrayList<>();
		ArrayList<ItemQuantity> listD = new ArrayList<>();
		ArrayList<ItemQuantity> listE = new ArrayList<>();
		ArrayList<ItemQuantity> listF = new ArrayList<>();
		
		listA.add(new ItemQuantity(yazoo, 1));
		listB.add(new ItemQuantity(lego, 1));
		listC.add(new ItemQuantity(crackers, 1));
		// listA.add(new ItemQuantity(yazoo, 1));
		// listA.add(new ItemQuantity(crackers, 5));

		//listD.add(new ItemQuantity(crackers, 3));
		//listE.add(new ItemQuantity(lego, 5));
		//listF.add(new ItemQuantity(yazoo, 1));
		//listF.add(new ItemQuantity(crackers, 1));
		//listF.add(new ItemQuantity(yazoo, 2));
		
		jobA.add(new Job(0, listA, 25, 0));
		jobB.add(new Job(0, listB, 10, 0));
		jobC.add(new Job(0, listC, 5, 0));
		
		//jobA.add(new Job(3, listD, 15, 0));

		
		//listB.add(new ItemQuantity(crackers, 10));
		
		
		// listB.add(new ItemQuantity(yazoo, 1));

		//listC.add(new ItemQuantity(lego, 2));

		//jobB.add(new Job(1, listB, 75, 0));
		//jobC.add(new Job(2, listC, 20, 0));
		//jobC.add(new Job(4, listE, 50, 0));
		//jobB.add(new Job(5, listF, 30, 0));

		map1.put(robotA, jobA);
		map1.put(robotB, jobB);
		map1.put(robotC, jobC);

		plannerA = new MultiRoutePlanner(mapA, 60f, map1, bases, 1000);


		for (Entry<Robot, LinkedList<Job>> entry : map1.entrySet()) {

			System.out.println(entry.getKey().getName() + " will pick:");

			for (Job job : entry.getValue()) {
				for (ItemQuantity item : job.getItems()) {
					System.out.println(item.getItem().getName() + ": " + item.getItem().getJunction());
				}
			}

			if (entry.getValue().size() == 0) {
				System.out.println("nothing");
			}

		}

		plannerA.computeCommands();

		System.out.println(robotA.getName() + ": " + plannerA.getCommands(robotA).getCommands());
		System.out.println(robotB.getName() + ": " + plannerA.getCommands(robotB).getCommands());
		System.out.println(robotC.getName() + ": " + plannerA.getCommands(robotC).getCommands());

		ArrayDeque<Command> commandsA = plannerA.getCommands(robotA).getCommands();
		ArrayDeque<Command> commandsB = plannerA.getCommands(robotB).getCommands();
		ArrayDeque<Command> commandsC = plannerA.getCommands(robotC).getCommands();

		LinkedList<Junction> junctionsA = plannerA.getCommands(robotA).getJunctions();
		LinkedList<Junction> junctionsB = plannerA.getCommands(robotB).getJunctions();
		LinkedList<Junction> junctionsC = plannerA.getCommands(robotC).getJunctions();

		double longestList = Math.max(junctionsA.size(), Math.max(junctionsB.size(), junctionsC.size()));
		
		Junction A = junctionsA.get(0);
		Junction B = junctionsB.get(0);
		Junction C = junctionsC.get(0);

		boolean fail = false;
		
		for (int i = 0; i < longestList; i++) {

			try {
				A = junctionsA.get(i);

			} catch (IndexOutOfBoundsException e) {}
			
			try {
				B = junctionsB.get(i);
			} catch (IndexOutOfBoundsException e) {
			}

			try {
				C = junctionsC.get(i);
			} catch (IndexOutOfBoundsException e) {
			}

			
			if(A.getX() == B.getX() && A.getY() == B.getY()){
				System.out.println("A and B clash: " + A + ", " + B);
				fail = true;
			}
			
			if(A.getX() == C.getX() && A.getY() == C.getY()){
				System.out.println("A and C clash: " + A + ", " + C);
				fail = true;
			}
			
			if(C.getX() == B.getX() && C.getY() == B.getY()){
				System.out.println("B and C clash: " + B + ", " + C);
				fail = true;
			}
			
			System.out.println("timestep: " + i + " - " + A + " " + B + " " + C);

		}

		if(!fail){
		System.out.println("pass!");
		}
		else
		{
			System.out.println("failed...");
		}
		System.exit(0);

	}

}
