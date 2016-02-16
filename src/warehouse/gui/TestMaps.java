package warehouse.gui;

import java.awt.geom.Rectangle2D;

public class TestMaps {
	static {
		TEST_MAP1 = new Map(4, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1, 1),
			new Rectangle2D.Double(2.5, 2.5, 1, 1),
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
}
