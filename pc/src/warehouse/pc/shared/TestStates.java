package warehouse.pc.shared;

import warehouse.shared.robot.Robot;

public class TestStates {
	static {
		TEST_STATE1 = new State(TestMaps.TEST_MAP1, new Robot[] {
			new Robot("Jeff" , "0", 0, 0, 90.0),
			new Robot("Nigel", "0", 3, 0, 0.0),
			new Robot("Dave" , "0", 0, 3, 180.0 + 45.0),
			new Robot("Other", "0", 4, 2, 270.0 + 45.0),
		});
		TEST_STATE2 = new State(TestMaps.TEST_MAP2, new Robot[] {
			new Robot("Jeff" , "0", 5, 5, 19.0),
			new Robot("Nigel", "0", 3, 0, 0.0),
			new Robot("Dave" , "0", 0, 3, 180.0 + 45.0),
			new Robot("Other", "0", 4, 2, 270.0 + 45.0),
		});
		TEST_STATE3 = new State(TestMaps.TEST_MAP3, new Robot[] {
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
