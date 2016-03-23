package warehouse.pc.search;

import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
		
		t.test1();
	}
	
	@Test
	public void test1() {
		MainInterface mi = MainInterface.get();
		Map map = TestMaps.PATHFINDING_TEST;
		
		ArrayList<Pair<Junction, Junction>> routeInfo = new ArrayList<>();
		routeInfo.add(Pair.makePair(new Junction(2, 0), new Junction(5, 0)));
		routeInfo.add(Pair.makePair(new Junction(6, 0), new Junction(3, 0)));
		
		ArrayList<Optional<LinkedList<Command>>> routeResults = new ArrayList<>();
		CMultiRouteFinder finder = new CMultiRouteFinder(map, new RouteFinder(map));
		CReserveTable reserve = new CReserveTable();
		for (Pair<Junction, Junction> p : routeInfo) {
			Junction start = p.getItem1();
			Junction goal = p.getItem2();
			
			Optional<LinkedList<Command>> op = finder.findRoute(start, goal, reserve, 0);
			if (op.isPresent()) {
				reserve.reservePositions(start, op.get() , 0);
				reserve.reservePositionAfter(goal, op.get().size());
			}
			routeResults.add(op);
		}
		
		for (Optional<LinkedList<Command>> res : routeResults) {
			System.out.println(res);
		}
		
		CommandType[] path1 = new CommandType[] {
			CommandType.X_POS,
			CommandType.X_POS,
			CommandType.X_POS,
		};
		CommandType[] path2 = new CommandType[] {
			CommandType.X_POS,
			CommandType.Y_POS,
			CommandType.Y_POS,
			CommandType.Y_POS,
			CommandType.X_NEG,
			CommandType.X_NEG,
			CommandType.Y_NEG,
			CommandType.X_NEG,
			CommandType.X_NEG,
			CommandType.X_NEG,
			CommandType.Y_NEG,
			CommandType.Y_NEG,
			CommandType.X_POS,
		};
		
		assertTrue(path1.length == routeResults.get(0).get().size());
		assertTrue(path2.length == routeResults.get(1).get().size());
		for (int i = 0; i < path1.length; i++) {
			assertTrue(path1[i].equals(routeResults.get(0).get().get(i).getType()));
		}
		for (int i = 0; i < path2.length; i++) {
			assertTrue(path2[i].equals(routeResults.get(1).get().get(i).getType()));
		}
		
		ArrayList<Job> jobList = mi.getJobList().getList();
		jobList.remove(0);
	}
}
