package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Test;

import rp.util.Pair;
import warehouse.pc.job.Item;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandQueue;
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
//		t.test1();
//		t.test2();
//		t.test3();
		
		// CMultiRoutePlanner tests
		t.test4();
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
	
	private void testRoutes(Map map, ArrayList<Pair<Junction, Junction>> routeInfo) {
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
		
		validateRoutes(map, routeInfo, routeResults, true);
	}
	
	/**
	 * Simulate path to assert that all positions are valid
	 * Also assert that no robots hit each other.
	 * 
	 * @param map the map that was used
	 * @param routeInfo a list of robots with their start and end locations
	 * @param routes the results of the route finder
	 * @param staticRobots if false then doesn't check for collisions after a robot has finished it's commands
	 */
	private void validateRoutes(Map map, ArrayList<Pair<Junction, Junction>> routeInfo, ArrayList<Optional<LinkedList<Command>>> routes, boolean staticRobots) {
		assertTrue(routes.size() == routeInfo.size());
		
		// x, y positions of all robots
		ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
		for (Pair<Junction, Junction> info : routeInfo) {
			// Add robots at start
			positions.add(Pair.makePair(info.getItem1().getX(), info.getItem1().getY()));
		}
		printPositions(positions);
		ArrayList<Pair<Integer, Integer>> prevPositions = new ArrayList<>(positions);
		
		ArrayList<Boolean> robotEnded = new ArrayList<>();
		for (int i = 0; i < routeInfo.size(); i++) {
			robotEnded.add(false);
		}
		
		for (int i = 0; ; i++) {
			// Update positions
			for (int r = 0; r < routes.size(); r++) {
				Optional<LinkedList<Command>> res = routes.get(r);
				if (res.isPresent()) {
					if (i < res.get().size()) {
						Command com = res.get().get(i);
						com.setFrom(positions.get(r).getItem1(), positions.get(r).getItem2());
						positions.set(r, Pair.makePair(com.getX(), com.getY()));
					} else {
						robotEnded.set(r, true);
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
			for (int r1 = 0; r1 < routes.size(); r1++) {
				for (int r2 = r1 + 1; r2 < routes.size(); r2++) {
					// Ignore collisions between ended robots and normal robots if the robots aren't static
					if (!staticRobots && (robotEnded.get(r1) || robotEnded.get(r2))) {
						continue;
					}
					boolean xEqual = positions.get(r1).getItem1().equals(positions.get(r2).getItem1());
					boolean yEqual = positions.get(r1).getItem2().equals(positions.get(r2).getItem2());
					
					assertTrue("Robots have collided: " + r1 + " and " + r2 + " at " + positions.get(r1).getItem1() + ", " + positions.get(r1).getItem2(),
						!(xEqual && yEqual));
				}
			}
			
			// If all robots have reached the end of their commands, exit.
			boolean exit = true;
			for (Optional<LinkedList<Command>> res : routes) {
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
			if (!routes.get(r).isPresent())
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

	@Test
	public void test4() {
		System.out.println("=== Test 4 ===");
		Map map = TestMaps.TEST_MAP1;
		ArrayList<Junction> bases = new ArrayList<>();
		bases.add(new Junction(2, 0));
		bases.add(new Junction(4, 2));
		
		Item a = new Item("a", 10.0f, 8.0f, 0, 0);
		Item b = new Item("b", 10.0f, 8.0f, 5, 0);
		Item c = new Item("c", 10.0f, 8.0f, 7, 0);
		
		ArrayList<ItemQuantity> jeffItems = new ArrayList<>();
		jeffItems.add(new ItemQuantity(a, 6));
		jeffItems.add(new ItemQuantity(b, 3));
		
		ArrayList<ItemQuantity> daveItems = new ArrayList<>();
		daveItems.add(new ItemQuantity(b, 6));
		daveItems.add(new ItemQuantity(c, 3));
		
		ArrayList<Pair<Robot, Job>> robots = new ArrayList<>();
		robots.add(Pair.makePair(new Robot("Jeff", "", 2, 1, 0), new Job(100, jeffItems)));
		robots.add(Pair.makePair(new Robot("Dave", "", 2, 1, 0), new Job(100, daveItems)));
		
		testPlans(map, bases, robots);
	}
	
	private void testPlans(Map map, ArrayList<Junction> bases, ArrayList<Pair<Robot, Job>> jobs) {
		CMultiRoutePlanner planner = new CMultiRoutePlanner(map, bases, new RouteFinder(map), new CReserveTable());
		
		ArrayList<Pair<Robot, LinkedList<Command>>> commands = new ArrayList<>();
		for (Pair<Robot, Job> p : jobs) {
			LinkedList<Job> l = new LinkedList<>();
			l.add(p.getItem2());
			long t0 = System.currentTimeMillis();
			LinkedList<Command> coms = planner.routeRobot(p.getItem1(), l);
			long t1 = System.currentTimeMillis();
			commands.add(Pair.makePair(p.getItem1(), coms));
			System.out.printf("%s: %4dms: job %s: %s\n", p.getItem1().getIdentity(), t1 - t0, p.getItem2(), coms);
		}
		
		HashMap<Robot, Pair<LinkedList<Job>, LinkedList<Command>>> robots = new HashMap<>();
		for (int i = 0; i < jobs.size(); i++) {
			LinkedList<Job> l = new LinkedList<>();
			l.add(jobs.get(i).getItem2());
			robots.put(jobs.get(i).getItem1(),
				Pair.makePair(l, commands.get(i).getItem2()));
		}
		validatePlan(map, robots);
	}
	
	/**
	 * Validates the plan provided
	 *   - Makes sure the path is correct
	 *       - removes them from the simulation after the robots are done.
	 *   - Makes sure the jobs have been completed
	 *   - Makes sure the robots never go over their weight limit
	 */
	private void validatePlan(Map map, ArrayList<Pair<Robot, LinkedList<Job>>> jobs, ArrayList<Pair<Robot, LinkedList<Command>>> commands) {
		assertTrue(jobs.size() == commands.size());
		
		// Validate path
		ArrayList<Optional<LinkedList<Command>>> routes = new ArrayList<>();
		for (Pair<Robot, LinkedList<Command>> e : commands) {
			routes.add(Optional.of(e.getItem2()));
		}
		validateRoutes(map, null, routes, false);
		
		// Makes sure jobs have been completed.
		for (int i = 0; i < jobs.size(); i++) {
			int count = 0;
			for (Command c : commands.get(i).getItem2()) {
				if (c.getType() == CommandType.COMPLETE_JOB)
					count++;
			}
			assertTrue("Not all jobs have been completed by " + jobs.get(i).getItem1().getIdentity() + ".", count == jobs.get(i).getItem2().size());
		}
		
		
	}
}
