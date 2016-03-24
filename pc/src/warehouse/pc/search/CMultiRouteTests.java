package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import rp.util.Pair;
import warehouse.pc.job.Item;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandType;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;

public class CMultiRouteTests {
	public static void main(String[] args) {
		CMultiRouteTests t = new CMultiRouteTests();
		
		// CMultiRouteFinder tests
		t.test1();
		t.test2();
		t.test3();
		
		
	}
	
	@Test
	public void test1() {
		System.out.println("=== Test 1 ===");
		Map map = TestMaps.PATHFINDING_TEST;
		
		ArrayList<Pair<Junction, Junction>> routeInfo = new ArrayList<>();
		routeInfo.add(Pair.makePair(new Junction(2, 0), new Junction(5, 0)));
		routeInfo.add(Pair.makePair(new Junction(6, 0), new Junction(3, 0)));
		
		testRoutes(map, routeInfo);
	}
	
	@Test
	public void test2() {
		System.out.println("=== Test 2 ===");
		Map map = TestMaps.TEST_MAP3;
		
		ArrayList<Pair<Junction, Junction>> routeInfo = new ArrayList<>();
		routeInfo.add(Pair.makePair(new Junction(0, 0), new Junction(5, 0)));
		routeInfo.add(Pair.makePair(new Junction(3, 0), new Junction(0, 0)));
		
		testRoutes(map, routeInfo);
	}
	
	@Test
	public void test3() {
		// 3 robots this time
		System.out.println("=== Test 3 ===");
		Map map = TestMaps.TEST_MAP3;
		
		ArrayList<Pair<Junction, Junction>> routeInfo = new ArrayList<>();
		routeInfo.add(Pair.makePair(new Junction(5, 0), new Junction(0, 0)));
		routeInfo.add(Pair.makePair(new Junction(5, 3), new Junction(0, 3)));
		// This robot would be able to path if the maximum waits was 4.
		// However, it is 3 for performance reasons
		routeInfo.add(Pair.makePair(new Junction(4, 3), new Junction(3, 1)));
		
		testRoutes(map, routeInfo);
	}
	
	public void testRoutes(Map map, ArrayList<Pair<Junction, Junction>> routeInfo) {
		ArrayList<Optional<LinkedList<Command>>> routeResults = new ArrayList<>();
		CMultiRouteFinder finder = new CMultiRouteFinder(map, new RouteFinder(map));
		CReserveTable reserve = new CReserveTable();
		int i = 0;
		long tStart = System.currentTimeMillis();
		for (Pair<Junction, Junction> p : routeInfo) {
			Junction start = p.getItem1();
			Junction goal = p.getItem2();
			
			long t0 = System.currentTimeMillis();
			Optional<LinkedList<Command>> op = finder.findRoute(start, goal, reserve, 0);
			long t1 = System.currentTimeMillis();
			if (op.isPresent()) {
				reserve.reservePositions(start, op.get() , 0);
				reserve.reservePositionAfter(goal, op.get().size() - 1);
			} else {
				reserve.reservePositionAfter(start, 0);
			}
			routeResults.add(op);
			System.out.printf("Robot %d: %4dms: %s\n", i, t1 - t0, op);
			i++;
		}
		long tEnd = System.currentTimeMillis();
		long ms = tEnd - tStart;
		System.out.println("Total: " + ms + "ms");
		
		validateResults(map, routeInfo, routeResults);
	}
	
	/**
	 * Simulate path to assert that all positions are valid
	 * Also assert that no robots hit each other.
	 * 
	 * @param map the map that was used
	 * @param routeInfo a list of robots with their start and end locations
	 * @param results the results of the route finder
	 */
	public void validateResults(Map map, ArrayList<Pair<Junction, Junction>> routeInfo, ArrayList<Optional<LinkedList<Command>>> results) {
		assertTrue(results.size() == routeInfo.size());
		
		// x, y positions of all robots
		ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
		for (Pair<Junction, Junction> info : routeInfo) {
			// Add robots at start
			positions.add(Pair.makePair(info.getItem1().getX(), info.getItem1().getY()));
		}
		printPositions(positions);
		ArrayList<Pair<Integer, Integer>> prevPositions = new ArrayList<>(positions);
		
		for (int i = 0; ; i++) {
			// Update positions
			for (int r = 0; r < results.size(); r++) {
				Optional<LinkedList<Command>> res = results.get(r);
				if (res.isPresent()) {
					// Update position
					if (i < res.get().size()) {
						Command com = res.get().get(i);
						com.setFrom(positions.get(r).getItem1(), positions.get(r).getItem2());
						positions.set(r, Pair.makePair(com.getX(), com.getY()));
					}
				}
			}
			
			// Print positions
			printPositions(positions);
			
			// Check if positions are valid
			for (int r = 0; r < positions.size(); r++) {
				Pair<Integer, Integer> pos = positions.get(r);
				Junction j = map.getJunction(pos.getItem1(), pos.getItem2());
				assertTrue("Junction at " + pos.getItem1() + ", " + pos.getItem2() + " is not valid.", j != null);
			}
			
			// Check that there is a link between the previous junction and here
			for (int r = 0; r < positions.size(); r++) {
				Pair<Integer, Integer> prev = prevPositions.get(r);
				Pair<Integer, Integer> pos = positions.get(r);
				
				Junction from = map.getJunction(prev.getItem1(), prev.getItem2());
				Junction to = map.getJunction(pos.getItem1(), pos.getItem2());
				if (from.equals(to)) {
					continue; // There is always a link from a junction to itself!
				}
				
				boolean found = false;
				for (Junction n : from.getNeighbours()) {
					if (n != null && n.equals(to)) {
						found = true;
						break;
					}
				}
				
				assertTrue("Cannot get to " + to + " from " + from + ".", found);
			}
			
			// Check that the positions don't collide
			for (int r1 = 0; r1 < results.size(); r1++) {
				for (int r2 = r1 + 1; r2 < results.size(); r2++) {
					boolean xEqual = positions.get(r1).getItem1().equals(positions.get(r2).getItem1());
					boolean yEqual = positions.get(r1).getItem2().equals(positions.get(r2).getItem2());
					
					assertTrue("Robots have collided: " + r1 + " and " + r2 + " at " + positions.get(r1).getItem1() + ", " + positions.get(r1).getItem2(),
						!(xEqual && yEqual));
				}
			}
			
			// If all robots have reached the end of their commands, exit.
			boolean exit = true;
			for (Optional<LinkedList<Command>> res : results) {
				if (res.isPresent()) {
					if (i < res.get().size() - 1) {
						exit = false;
					}
				}
			}
			
			if (exit)
				break;
			
			prevPositions = new ArrayList<>(positions);
		}
		
		// Check all robots that had commands ended up at their goals
		for (int r = 0; r < positions.size(); r++) {
			if (!results.get(r).isPresent())
				continue;
			
			Pair<Integer, Integer> pos = positions.get(r);
			Junction goal = routeInfo.get(r).getItem2();
			int x = pos.getItem1();
			int y = pos.getItem2();
			
			boolean xEqual = x == goal.getX();
			boolean yEqual = y == goal.getY();
			assertTrue("Robot " + r + " is at " + x + ", " + y + " but should be at " + goal + ".", xEqual && yEqual);
		}
	}
	
	private void printPositions(List<Pair<Integer, Integer>> positions) {
		System.out.print("Positions: [");
		int i = 0;
		for (Pair<Integer, Integer> pos : positions) {
			System.out.print("[" + pos.getItem1() + ", " + pos.getItem2() + "]");
			
			if (i < positions.size() - 1) {
				System.out.print(", ");
			}
			
			i++;
		}
		System.out.println("]");
	}
}
