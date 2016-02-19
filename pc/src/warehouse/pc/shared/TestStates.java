package warehouse.pc.shared;

import java.awt.geom.Rectangle2D;

import warehouse.pc.shared.robot.Robot;

public class TestStates {
	static {
		TEST_STATE1 = new State(new Map(8, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 0.5, 1, 1),
			new Rectangle2D.Double(2.5, 2.5, 1, 0.5),
			new Rectangle2D.Double(4.5, 1.5, 2, 1),
		}), new Robot[] {
			new Robot("Jeff" , "0", 0, 0, 90.0),
			new Robot("Nigel", "0", 3, 0, 0.0),
			new Robot("Dave" , "0", 0, 3, 180.0 + 45.0),
			new Robot("Other", "0", 4, 2, 270.0 + 45.0),
		});
		TEST_STATE2 = new State(new Map(7, 7, new Rectangle2D.Double[] {
			new Rectangle2D.Double(0.5, 1.5, 1, 3),
			new Rectangle2D.Double(2.5, 1.5, 1, 3),
			new Rectangle2D.Double(4.5, 1.5, 1, 3),
		}), new Robot[] {
			new Robot("Jeff" , "0", 5, 5, 19.0),
			new Robot("Nigel", "0", 3, 0, 0.0),
			new Robot("Dave" , "0", 0, 3, 180.0 + 45.0),
			new Robot("Other", "0", 4, 2, 270.0 + 45.0),
		});
		TEST_STATE3 = new State(new Map(6, 4, new Rectangle2D.Double[] {
			new Rectangle2D.Double(1.4, 0.4, 0.2, 1.2),
			new Rectangle2D.Double(1.6, 1.4, 1.0, 0.2),
			new Rectangle2D.Double(0.4, 1.4, 0.2, 1.2),
			new Rectangle2D.Double(1.4, 2.4, 1.2, 0.2),
			new Rectangle2D.Double(3.4, 1.4, 0.2, 1.6),
			new Rectangle2D.Double(4.4, 2.4, 0.2, 0.6),
			new Rectangle2D.Double(4.4, 0.0, 0.2, 0.6),
		}), new Robot[] {
			new Robot("Jeff" , "0", 3, 3, 19.0),
			new Robot("Nigel", "0", 3, 0, 0.0),
			new Robot("Dave" , "0", 0, 3, 180.0 + 45.0),
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
	public final static State TEST_STATE1;
	
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
	public final static State TEST_STATE2;
	
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
	public final static State TEST_STATE3;
}
