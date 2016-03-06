package warehouse.pc.search;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.pc.job.Item;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Bearing;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;
import warehouse.shared.robot.Robot;

public class PlanningTests {

	Map tm1;
	Map tm2;
	Map tm3;
	Map tm4;
	
	Direction xp = Direction.X_POS;
	Direction xn = Direction.X_NEG;
	Direction yp = Direction.Y_POS;
	Direction yn = Direction.Y_NEG;
	
	Bearing r = Bearing.RIGHT;
	Bearing l = Bearing.LEFT;
	Bearing f = Bearing.FORWARD;
	Bearing b = Bearing.BACKWARD;
	
	RoutePlanner tpl;
	RoutePlanner tp2;
	RoutePlanner tp3;
	RoutePlanner tp4;
	
	@Before
	public void setUp() throws Exception {
		
		tm1 = TestMaps.TEST_MAP1;
		tm2 = TestMaps.TEST_MAP2;
		tm3 = TestMaps.TEST_MAP3;
		tm4 = TestMaps.TEST_MAP4;
		
		HashMap<Robot, LinkedList<Job>> pj = new HashMap<Robot, LinkedList<Job>>();
		Item heavy = new Item("heavy", 10, 20f, 1, 3);
		Item medium = new Item("medium", 10, 10f, 2, 1);
		Item light = new Item("light", 10, 5f, 0, 0);
		
		Job testJob = new Job(0, _items, 60, 30)
		
		ArrayList<Junction> bases = new ArrayList<>();
		
		tpl = new RoutePlanner(tm1, 50f, pj, bases);
		tp2 = new RoutePlanner(tm2, 50f, pj, bases);
		tp3 = new RoutePlanner(tm3, 50f, pj, bases);
		tp4 = new RoutePlanner(tm4, 50f, pj, bases);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
