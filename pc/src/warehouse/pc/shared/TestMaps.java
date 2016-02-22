package warehouse.pc.shared;

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
		TEST_MAP3 = new Map(6, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(1.4, 0.4, 0.2, 1.2),
			new Rectangle2D.Double(1.6, 1.4, 1.0, 0.2),
			new Rectangle2D.Double(0.4, 1.4, 0.2, 1.2),
			new Rectangle2D.Double(1.4, 2.4, 1.2, 0.2),
			new Rectangle2D.Double(3.4, 1.4, 0.2, 1.6),
			new Rectangle2D.Double(4.4, 2.4, 0.2, 0.6),
			new Rectangle2D.Double(4.4, 0.0, 0.2, 0.6),
		});
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
}
