package warehouse.pc.localisation;

import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.TestMaps;

public class LocalisationTest {
	public static void main(String[] args) {
		Map map = TestMaps.REAL_WAREHOUSE;
		
		Robot r = new Robot("Jeff", "", 0, 0, 90);
		Localisation l = new Localisation(map, r.getDirection());
		
		System.out.println(l);
		
		l.distanceRecieved(200);
		
		System.out.println(l);
	}
}
