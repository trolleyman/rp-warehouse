package warehouse.pc.shared;

import java.awt.geom.Rectangle2D;

import rp.robotics.mapping.MapUtils;

public class TestMaps {
	static {
		TEST_MAP1 = new Map(8, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1, 1),
			new Rectangle2D.Double(2.5, 2.5, 1, 0.5),
			new Rectangle2D.Double(4.5, 1.5, 2, 1),
		}, 1.0f);
		TEST_MAP2 = new Map(7, 7, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 1.5, 1, 3),
			new Rectangle2D.Double(2.5, 1.5, 1, 3),
			new Rectangle2D.Double(4.5, 1.5, 1, 3),
		}, 0.1f);
		TEST_MAP3 = new Map(6, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(1.4, 0.4, 0.2, 1.2),
			new Rectangle2D.Double(1.6, 1.4, 1.0, 0.2),
			new Rectangle2D.Double(0.4, 1.4, 0.2, 1.2),
			new Rectangle2D.Double(1.4, 2.4, 1.2, 0.2),
			new Rectangle2D.Double(3.4, 1.4, 0.2, 1.6),
			new Rectangle2D.Double(4.4, 2.4, 0.2, 0.6),
			new Rectangle2D.Double(4.4, 0.0, 0.2, 0.6),
		}, 0.5f);
		TEST_MAP4 = new Map(12, 8, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1.0, 5.0),
			new Rectangle2D.Double(3.5, 0.5, 1.0, 5.0),
			new Rectangle2D.Double(6.5, 0.5, 1.0, 5.0),
			new Rectangle2D.Double(9.5, 0.5, 1.0, 5.0),
		}, 0.25f);
		REAL_WAREHOUSE = new Map(MapUtils.createRealWarehouse());
		MARKING_WAREHOUSE = new Map(MapUtils.createMarkingWarehouseMap());
		PATHFINDING_TEST = new Map(8, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(2.5, 0.5, 4, 1),
			new Rectangle2D.Double(5.5, 1.5, 1, 1),
		}, 0.1f);
	}
	
	/**
	 * <pre>
	 * +---+---+       +---+---+---+
	 * |   |   |       |           |
	 * +---+---+---+---+           +
	 * |       |   |   |           |
	 * +       +---+---+---+---+---+
	 * |       |   |   |   |   |   |
	 * +---+---+---+---+---+---+---+
	 * </pre>
	 */
	public final static Map TEST_MAP1;
	
	/**
	 * <pre>
	 * +---+---+---+---+---+---+
	 * |   |   |   |   |   |   |
	 * +---+---+---+---+---+---+
	 * |       |       |       |
	 * +       +       +       +
	 * |       |       |       |
	 * +       +       +       +
	 * |       |       |       |
	 * +       +       +       +
	 * |       |       |       |
	 * +---+---+---+---+---+---+
	 * |   |   |   |   |   |   |
	 * +---+---+---+---+---+---+
	 * </pre>
	 */
	public final static Map TEST_MAP2;
	
	/**
	 * <pre>
	 * +---+---+---+   +   +
	 * |   |       |   |   |
	 * +   +---+---+   +---+
	 * |   |       |   |   |
	 * +---+   +---+---+---+
	 * |   |   |   |   |   |
	 * +---+---+---+---+   +
	 * </pre>
	 */
	public final static Map TEST_MAP3;
	
	/**
	 * <pre>
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * |   |   |   |   |   |   |   |   |   |   |   |
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * |       |   |       |   |       |   |       |
	 * +       +---+       +---+       +---+       +
	 * |       |   |       |   |       |   |       |
	 * +       +---+       +---+       +---+       +
	 * |       |   |       |   |       |   |       |
	 * +       +---+       +---+       +---+       +
	 * |       |   |       |   |       |   |       |
	 * +       +---+       +---+       +---+       +
	 * |       |   |       |   |       |   |       |
	 * +       +---+       +---+       +---+       +
	 * |       |   |       |   |       |   |       |
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * </pre>
	 */
	public final static Map TEST_MAP4;
	
	/**
	 * Like TEST_MAP4. But real! - This is the training map
	 */
	public final static Map REAL_WAREHOUSE;
	
	/**
	 * This is the marking map
	 * <pre>
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * |   |   |   |   |   |   |   |   |   |   |   |
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * |   |       |   |   |   |   |   |       |   |
	 * +---+       +---+---+---+---+---+       +---+
	 * |   |       |   |           |   |       |   |
	 * +---+       +---+           +---+       +---+
	 * |   |       |   |           |   |       |   |
	 * +---+       +---+           +---+       +---+
	 * |   |       |   |           |   |       |   |
	 * +---+       +---+---+---+---+---+       +---+
	 * |   |       |   |   |   |   |   |       |   |
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * |   |   |   |   |   |   |   |   |   |   |   |
	 * +---+---+---+---+---+---+---+---+---+---+---+
	 * </pre>
	 */
	public final static Map MARKING_WAREHOUSE;
	
	/**
	 * Pathfinding test map
	 * http://imgur.com/kE8xENd - Like in the lecture
	 * <pre>
	 * 3 +---+---+---+---+---+---+---+
	 *   |   |   |   |   |   |   |   |
	 * 2 +---+---+---+---+---+---+   +
	 *   |   |   |                   |
	 * 1 +---+---+                   +
	 *   |   |   |                   |
	 * 0 +---+---A---2---+---1---B---+
	 *   0   1   2   3   4   5   6   7
	 * </pre>
	 * With robot A trying to get to 1 and robot B trying to get to 2.
	 */
	public final static Map PATHFINDING_TEST;
}
