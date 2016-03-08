package warehouse.pc.search;

import java.util.ArrayList;
import java.util.LinkedList;

import warehouse.pc.shared.Command;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;

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
		
		ArrayList<Direction> testArray = testSearch.findRoute(testMap.getJunction(5, 1), testMap.getJunction(4, 0),
				Direction.X_POS);
		LinkedList<Command> directions = testSearch.getActualDirections(testArray, Direction.X_POS);

		// prints a list of bearings
		
		for (int i = 0; i < testArray.size(); i++) {
			System.out.println("[" + directions.get(i) + "], ");

		}

	}

}
