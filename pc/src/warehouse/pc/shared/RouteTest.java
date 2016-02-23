package warehouse.pc.shared;

import java.util.ArrayList;

public class RouteTest {

	public static void main(String[] args) {
		
		Map testMap = TestMaps.TEST_MAP3;
		
		
		
		RouteFinder testSearch = new RouteFinder(testMap);
		
		ArrayList<Direction> testArray = testSearch.findRoute(testMap.getJunction(0, 2), testMap.getJunction(4, 3));
		
		for (int i = 0; i < testArray.size(); i++)
		{
			System.out.println("[" + testArray.get(i) + "], ");
			
		}
		
	}

}
