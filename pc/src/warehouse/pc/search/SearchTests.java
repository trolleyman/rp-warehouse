package warehouse.pc.search;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.pc.shared.Bearing;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;

public class SearchTests {

	RouteFinder ts1;
	RouteFinder ts2;
	RouteFinder ts3;
	RouteFinder ts4;
	Map tm1;
	Map tm2;
	Map tm3;
	Map tm4;
	final boolean alexIsHelping = false;
	
	Direction xp = Direction.X_POS;
	Direction xn = Direction.X_NEG;
	Direction yp = Direction.Y_POS;
	Direction yn = Direction.Y_NEG;
	
	Bearing r = Bearing.RIGHT;
	Bearing l = Bearing.LEFT;
	Bearing f = Bearing.FORWARD;
	Bearing b = Bearing.BACKWARD;
	
	@Before
	public void setUp() throws Exception {
		
		tm1 = TestMaps.TEST_MAP1;
		tm2 = TestMaps.TEST_MAP2;
		tm3 = TestMaps.TEST_MAP3;
		tm4 = TestMaps.TEST_MAP4;
		
		ts1 = new RouteFinder(tm1);
		ts2 = new RouteFinder(tm2);
		ts3 = new RouteFinder(tm3);
		ts4 = new RouteFinder(tm4);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Helper method to return a path
	 * @param finder the route finder
	 * @param map the test map
	 * @param startx the x start coordinate
	 * @param starty the y start coordinate
	 * @param goalx the x goal coordinate
	 * @param goaly the y goal coordinate
	 * @param direction the initial direction
	 * @return the linkedlist of the path
	 */
	
	private LinkedList<Bearing> testRoute(RouteFinder finder, Map map, int startx, int starty, int goalx, int goaly, Direction direction){
		
		Junction start = map.getJunction(startx, starty);
		Junction goal = map.getJunction(goalx, goaly);
		
		return finder.findRoute(start, goal, direction);			
		
		
	}
	
	 /*+---+---+---+   +   +
	   |   |       |   |   |
	   +   +---+---+   +---+
	   |   |       |   |   |
	   +---+   +---+---+---+
	   |   |   |   |   |   |
	   +---+---+---+---+   +  */
	
/*   +---+---+---+---+---+---+
	 |   |   |   |   |   |   |
	 +---+---+---+---+---+---+
	 |       |       |       |
	 +       +       +       +
	 |       |       |       |
	 +       +       +       +
	 |       |       |       |
	 +       +       +       +
	 |       |       |       |
	 +---+---+---+---+---+---+
	 |   |   |   |   |   |   |
	 +---+---+---+---+---+---+*/
	
	@Test
	public void test() {
		
		assertTrue(testRoute(ts1, tm1, 0, 0, 2, 0, yp).equals(Arrays.asList(r, f)));
		assertFalse(testRoute(ts1, tm1, 0, 0, 2, 0, xp).equals(Arrays.asList(r, f)));
		assertTrue(testRoute(ts3, tm3, 5, 0, 3, 3, yn).equals(Arrays.asList(b, l, f, r, f)));
		assertTrue(testRoute(ts3, tm3, 2, 1, 5, 3, xn).equals(Arrays.asList(b, f, l, r, l)));
		assertTrue(testRoute(ts2, tm2, 1, 6, 6, 4, yp).size() == 7);
		assertTrue(testRoute(ts2, tm2, 3, 0, 3, 6, yn).size() == 8);
		assertTrue(testRoute(ts1, tm1, 0, 0, 0, 0, yn).equals(Arrays.asList()));
		assertFalse(alexIsHelping);
		
	}
	
}
