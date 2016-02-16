package warehouse.gui;

import java.awt.geom.Rectangle2D;

public class TestMaps {
	static {
		TEST_MAP1 = new Map(8, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1, 1),
			new Rectangle2D.Double(2.5, 2.5, 1, 0.5),
			new Rectangle2D.Double(4.5, 1.5, 2, 1),
		});
		TEST_MAP2 = new Map(7, 7, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 1.5, 1, 3),
			new Rectangle2D.Double(2.5, 1.5, 1, 3),
			new Rectangle2D.Double(4.5, 1.5, 1, 3),
		});
	}
	
	/**
	 * +---+---+---+
	 * |       |   |
	 * +       +---+
	 * |       |   |
	 * +---+---+---+
	 * |   |   |
	 * +---+---+
	 */
	public final static Map TEST_MAP1;
	
	/**
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
	 */
	public final static Map TEST_MAP2;
}
