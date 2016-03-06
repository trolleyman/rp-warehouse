package warehouse.planning;

import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.LineMap;
import rp.robotics.mapping.MapUtils;

public class RouteTest {

	public static void main(String[] args) {
		
		LineMap lines = MapUtils.create2014Map2();
		
		// Nick's code from the test class
				int xJunctions = 10;
				int yJunctions = 7;

				float junctionSeparation = 30;

				int xInset = 14;
				int yInset = 31;

				GridMap gridMap = new GridMap(xJunctions, yJunctions, xInset, yInset,
						junctionSeparation, lines);
				
				
		WarehouseMap amazonPrime = new WarehouseMap(gridMap);
		
		amazonPrime.printMap();
		
		System.out.println("");
		
		int x = 0;
		int y = 0;
		int goalx = 5;
		int goaly = 4;
		
		
		amazonPrime.aStar(x, y, goalx, goaly, WarehouseMap.NORTH, true);
		
			
			

	}

}
