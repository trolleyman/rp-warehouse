package warehouse.pc.shared;

import java.util.ArrayList;

/**
 * Test class for route planning
 * @author Jason
 *
 */

public class RouteTest {

	public static void main(String[] args) {

		// change the test map
		
		Map testMap = TestMaps.TEST_MAP3;
		RouteFinder testSearch = new RouteFinder(testMap);

		// change the start node, goal node and initial direction
		
		ArrayList<Bearing> testArray = testSearch.findRoute(testMap.getJunction(2, 0), testMap.getJunction(0, 3),
				Direction.Y_NEG);

		// prints a list of bearings
		
		for (int i = 0; i < testArray.size(); i++) {
			System.out.println("[" + testArray.get(i) + "], ");

		}

	}

}
