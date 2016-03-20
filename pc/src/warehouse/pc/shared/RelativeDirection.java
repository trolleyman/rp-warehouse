package warehouse.pc.shared;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public enum RelativeDirection {
	FORWARD,
	RIGHT,
	BACKWARD,
	LEFT;
	
	@Test
	public static void test() {
		assertTrue(RelativeDirection.fromTo(Direction.Y_POS, Direction.Y_POS) == RelativeDirection.FORWARD);
		assertTrue(RelativeDirection.fromTo(Direction.Y_NEG, Direction.Y_NEG) == RelativeDirection.FORWARD);
		assertTrue(RelativeDirection.fromTo(Direction.X_POS, Direction.X_POS) == RelativeDirection.FORWARD);
		assertTrue(RelativeDirection.fromTo(Direction.X_NEG, Direction.X_NEG) == RelativeDirection.FORWARD);
		
		assertTrue(RelativeDirection.fromTo(Direction.Y_POS, Direction.X_POS) == RelativeDirection.RIGHT);
		assertTrue(RelativeDirection.fromTo(Direction.Y_POS, Direction.Y_NEG) == RelativeDirection.BACKWARD);
		assertTrue(RelativeDirection.fromTo(Direction.Y_POS, Direction.X_NEG) == RelativeDirection.LEFT);
		
		assertTrue(RelativeDirection.fromTo(Direction.X_NEG, Direction.Y_POS) == RelativeDirection.RIGHT);
		assertTrue(RelativeDirection.fromTo(Direction.X_NEG, Direction.X_POS) == RelativeDirection.BACKWARD);
		assertTrue(RelativeDirection.fromTo(Direction.X_NEG, Direction.Y_NEG) == RelativeDirection.LEFT);
	}
	
	/**
	 * Returns the RelativeDirection that would transform {@code from} into {@code to}
	 */
	public static RelativeDirection fromTo(Direction from, Direction to) {
		double diff = to.toFacing() - from.toFacing();
		diff %= 360.0;
		if (diff < 0.0)
			diff += 360.0;
		
		if (diff < 45.0)
			return RelativeDirection.FORWARD;
		else if (diff < 135.0)
			return RelativeDirection.RIGHT;
		else if (diff < 225.0)
			return RelativeDirection.BACKWARD;
		else if (diff < 315.0)
			return RelativeDirection.LEFT;
		else
			return RelativeDirection.FORWARD;
	}
}
