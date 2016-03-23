package warehouse.pc.search;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import rp.util.Pair;
import warehouse.pc.job.Item;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;

public class CRouteTests {
	public static void main(String[] args) {
		MainInterface mi = MainInterface.get();
		/**
		 * http://imgur.com/kE8xENd - Like in the lecture
		 * 
		 * +---+---+---+---+---+---+---+
		 * |   |   |   |   |   |   |   |
		 * +---+---+---+---+---+---+   +
		 * |   |   |                   |
		 * +---+---+                   +
		 * |   |   |                   |
		 * +---+---A---2---+---1---B---+
		 * 
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
		CRouteFinder finder = new CRouteFinder(map, new RouteFinder(map));
		CReserveTable reserve = new CReserveTable();
		for (Pair<Junction, Junction> p : routeInfo) {
			Junction start = p.getItem1();
			Junction goal = p.getItem2();
			
			Optional<LinkedList<Command>> op = finder.findRoute(start, goal, reserve);
			if (op.isPresent()) {
				reserve.reservePositions(start, op.get() , 0);
			}
			routeResults.add(op);
		}
		
		for (Optional<LinkedList<Command>> res : routeResults) {
			System.out.println(res);
		}
		
		ArrayList<Job> jobList = mi.getJobList().getList();
		jobList.remove(0);
	}
}
