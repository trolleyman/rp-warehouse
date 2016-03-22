package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;
import warehouse.pc.shared.Command;

/**
 * Testing the new multi robot route planner
 * @author George Kaye
 *
 */

public class Test {

	public static void main(String args[]){
		
		Robot robotA = new Robot("george", "george", 2, 0, 0);
		Robot robotB = new Robot("jason", "jason", 8, 0, 0);
		Robot robotC = new Robot("lenka", "lenka", 5, 0, 0);
		
		Map mapA = TestMaps.TEST_MAP4;
		
		Item yazoo = new Item("yazoo", 50, 10f, 2, 2);
		Item lego = new Item("lego", 20, 10f, 5, 2);
		Item crackers = new Item("crackers", 1, 10f, 8, 2);
		
		HashMap<Robot, LinkedList<Job>> map1 = new HashMap<>();
		ArrayList<Junction> bases = new ArrayList<>();
		
		bases.add(mapA.getJunction(0, 6));
		bases.add(mapA.getJunction(6, 6));
		
		LinkedList<Job> jobA = new LinkedList<>();
		LinkedList<Job> jobB = new LinkedList<>();
		LinkedList<Job> jobC = new LinkedList<>();
		
		ArrayList<ItemQuantity> listA = new ArrayList<>();
		ArrayList<ItemQuantity> listB = new ArrayList<>();
		ArrayList<ItemQuantity> listC = new ArrayList<>();
		
		listA.add(new ItemQuantity(yazoo, 1));
		listB.add(new ItemQuantity(lego, 1));
		listC.add(new ItemQuantity(crackers, 1));

		jobA.add(new Job(0, listA, 25, 0));
		jobB.add(new Job(0, listB, 10, 0));
		jobC.add(new Job(0, listC, 5, 0));
		
		map1.put(robotA, jobA);
		map1.put(robotB, jobB);
		map1.put(robotC, jobC);
		
		NewMultiRoutePlanner plannerA = new NewMultiRoutePlanner(mapA, 60f, map1, bases, 10, new HashMap<Junction, Command>());
		
		plannerA.computeCommands();
	}
	
	
	
}

