package warehouse.pc.shared;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Directions relative to the x and y axes of the map
 */

public enum Direction {
	Y_POS,
	X_POS,
	Y_NEG,
	X_NEG;
	
	static {
		test();
	}
	
	@Test
	public static void test() {
		// applyCommands relies on these assertions.
		assertTrue((Direction.Y_POS.ordinal() + 1) % 4 == Direction.X_POS.ordinal());
		assertTrue((Direction.X_POS.ordinal() + 1) % 4 == Direction.Y_NEG.ordinal());
		assertTrue((Direction.Y_NEG.ordinal() + 1) % 4 == Direction.X_NEG.ordinal());
		assertTrue((Direction.X_NEG.ordinal() + 1) % 4 == Direction.Y_POS.ordinal());
	}
	
	/**
	 * Converts a direction into a command
	 */
	public Command toCommand() {
		switch (this) {
		case Y_POS: return Command.Y_POS;
		case X_POS: return Command.X_POS;
		case Y_NEG: return Command.Y_NEG;
		case X_NEG: return Command.X_NEG;
		default: return Command.WAIT;
		}
	}
	
	/**
	 * Converts degrees from Y+ to a Direction.
	 */
	public static Direction fromFacing(double facing) {
		facing += 45.0;
		facing = facing % 360.0;
		if (facing < 0.0)
			facing = facing + 360.0;
		
		if (facing >= 0.0 && facing < 90.0) {
			return Direction.Y_POS;
		} else if (facing < 180.0) {
			return Direction.X_POS;
		} else if (facing < 270.0) {
			return Direction.Y_NEG;
		} else {
			return Direction.X_NEG;
		}
	}
	
	/**
	 * Converts a Direction to the number of degrees clockwise from Y+
	 */
	public double toFacing() {
		switch (this) {
		case Y_POS:
			return 0.0;
		case X_POS:
			return 90.0;
		case Y_NEG:
			return 180.0;
		case X_NEG:
			return 270.0;
		}
		return 0.0;
	}
}
