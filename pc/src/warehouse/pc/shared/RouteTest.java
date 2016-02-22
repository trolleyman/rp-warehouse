package warehouse.pc.shared;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class RouteTest {

	public static void main(String[] args) {
		
		Map testMap = new Map(6, 4, new Rectangle2D.Double[] {
				new Rectangle2D.Double(1.4, 0.4, 0.2, 1.2),
				new Rectangle2D.Double(1.6, 1.4, 1.0, 0.2),
				new Rectangle2D.Double(0.4, 1.4, 0.2, 1.2),
				new Rectangle2D.Double(1.4, 2.4, 1.2, 0.2),
				new Rectangle2D.Double(3.4, 1.4, 0.2, 1.6),
				new Rectangle2D.Double(4.4, 2.4, 0.2, 0.6),
				new Rectangle2D.Double(4.4, 0.0, 0.2, 0.6),
			});
		
		RouteFinder testSearch = new RouteFinder(testMap);
		
		ArrayList<Junction> testArray = testSearch.findRoute(testMap.getJunction(2, 1), testMap.getJunction(5, 3));
		
		for (int i = 0; i < testArray.size(); i++)
		{
			System.out.println("[" + testArray.get(i).getX() + "," + testArray.get(i).getY() + "], ");
			
		}
		
	}

}
