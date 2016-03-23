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
		test1();
	}
	
	@Test
	private static void test1() {
		MainInterface mi = MainInterface.get();
		/**
		 * http://imgur.com/kE8xENd - Like in the lecture
		 * 
		 * 3 +---+---+---+---+---+---+---+
		 *   |   |   |   |   |   |   |   |
		 * 2 +---+---+---+---+---+---+   +
		 *   |   |   |                   |
		 * 1 +---+---+                   +
		 *   |   |   |                   |
		 * 0 +---+---A---2---+---1---B---+
		 * 
		 *   0   1   2   3   4   5   6   7
		 * With robot A trying to get to 1 and robot B trying to get to 2.
		 */
		Map map = new Map(8, 4, new Rectangle.Double[] {
			new Rectangle.Double(1.5, 0.5, 4, 1),
			new Rectangle.Double(5.5, 1.5, 1, 1),
		}, mi.getMap().getCellSize());
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
			
		};
		
		ArrayList<Job> jobList = mi.getJobList().getList();
		jobList.remove(0);
	}
}
