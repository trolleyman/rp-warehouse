package warehouse.pc.shared;

import warehouse.shared.Direction;

public enum CommandType {
	Y_POS,
	X_POS,
	Y_NEG,
	X_NEG,
	PICK,
	DROP,
	COMPLETE_JOB,
	WAIT,
	FORWARD,
	BACKWARD,
	LEFT,
	RIGHT,
	WAIT_ESC;

	public static CommandType fromDirection(Direction dir) {
		switch (dir) {
		case Y_POS: return CommandType.Y_POS;
		case X_POS: return CommandType.X_POS;
		case Y_NEG: return CommandType.Y_NEG;
		case X_NEG: return CommandType.X_NEG;
		default: return CommandType.WAIT;
		}
	}
}
