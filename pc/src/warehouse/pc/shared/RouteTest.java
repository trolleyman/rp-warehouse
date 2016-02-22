package warehouse.pc.shared;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class RouteTest {

	public static void main(String[] args) {
		
		Map testMap = TestMaps.TEST_MAP3;
		
		RouteFinder testSearch = new RouteFinder(testMap);
		
		ArrayList<Junction> testArray = testSearch.findRoute(testMap.getJunction(2, 1), testMap.getJunction(5, 3));
		
		for (int i = 0; i < testArray.size(); i++)
		{
			System.out.println("[" + testArray.get(i).getX() + "," + testArray.get(i).getY() + "], ");
			
		}
		
	}

}
