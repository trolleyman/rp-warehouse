package warehouse.pc.search;

import java.util.ArrayList;
import java.util.LinkedList;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;
import warehouse.shared.Direction;

/**
 * Test class for route planning
 * @author Jason
 *
 */

public class RouteTest {

	public static void main(String[] args) {

		// change the test map
		
		Map testMap = TestMaps.TEST_MAP2;
		RouteFinder testSearch = new RouteFinder(testMap);

		// change the start node, goal node and initial direction
		
		RoutePackage routes = testSearch.findRoute(testMap.getJunction(5, 1), testMap.getJunction(4, 0),
				Direction.X_POS);
		ArrayList<Direction> testArray = routes.getDirectionList();
		LinkedList<Command> directions = routes.getCommandList();

		// prints a list of bearings
		
		for (Command c : directions) {
			System.out.println("[" + c + "], ");
		}
	}

}
