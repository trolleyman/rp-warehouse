package warehouse.shared;

import java.awt.geom.Rectangle2D;

public class TestStates {
	static {
		TEST_STATE1 = new State(new Map(8, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1, 1),
			new Rectangle2D.Double(2.5, 2.5, 1, 0.5),
			new Rectangle2D.Double(4.5, 1.5, 2, 1),
		}), new Robot[] {
			new Robot("Jeff" , 0, 0, 90.0),
			new Robot("Nigel", 3, 0, 0.0),
			new Robot("Dave" , 0, 3, 180.0 + 45.0),
			new Robot("Other", 4, 2, 270.0 + 45.0),
		});
		TEST_STATE2 = new State(new Map(7, 7, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 1.5, 1, 3),
			new Rectangle2D.Double(2.5, 1.5, 1, 3),
			new Rectangle2D.Double(4.5, 1.5, 1, 3),
		}), new Robot[] {
			new Robot("Jeff" , 5, 5, 19.0),
			new Robot("Nigel", 3, 0, 0.0),
			new Robot("Dave" , 0, 3, 180.0 + 45.0),
			new Robot("Other", 4, 2, 270.0 + 45.0),
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
	public final static State TEST_STATE1;
	
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
	public final static State TEST_STATE2;
}
