package warehouse.shared;

/**
 * Based on a co-ordinate system with 0,0 in the bottom-left.
 */
public enum Direction {
	YPos,
	YNeg,
	XPos,
	XNeg;
	
	/**
	 * Returns the new direction if turning left
	 */
	public Direction left() {
		switch (this) {
		case YPos: return XNeg;
		case XNeg: return YNeg;
		case YNeg: return XPos;
		case XPos: return YPos;
		}
		return this;
	}
	/**
	 * Returns the new direction if turning right
	 */
	public Direction right() {
		switch (this) {
		case YPos: return XPos;
		case XNeg: return YPos;
		case YNeg: return XNeg;
		case XPos: return YNeg;
		}
		return this;
	}
	/**
	 * Returns the new direction if turning 180 degrees
	 */
	public Direction backward() {
		switch (this) {
		case YPos: return YNeg;
		case YNeg: return YPos;
		case XPos: return XNeg;
		case XNeg: return XPos;
		}
		return this;
	}
}
