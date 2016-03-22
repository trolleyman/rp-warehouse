package warehouse.pc.shared;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

import warehouse.shared.Direction;

/**
 * Commands the robot can execute in the field
 */

public enum Command {
	Y_POS, X_POS, Y_NEG, X_NEG, FORWARD, // there was a reason these were here
	BACKWARD, // command is my baby
	LEFT, // mess with it and you mess with me
	RIGHT, // have a nice day
	PICK, DROP, COMPLETE_JOB, WAIT;

	// PICK and DROP are blocking commands:
	// robots should not proceed beyond these commands
	// until all three robots are ready to move

	// WAIT means the robot is waiting for the other robots
	// it should not proceed until all three robots are ready

	private int x;
	private int y;
	private int quantity;
	private float weight;

	public Optional<Direction> toDirection() {
		switch (this) {
		case Y_POS:
			return Optional.of(Direction.Y_POS);
		case X_POS:
			return Optional.of(Direction.X_POS);
		case Y_NEG:
			return Optional.of(Direction.Y_NEG);
		case X_NEG:
			return Optional.of(Direction.X_NEG);
		default:
			return Optional.empty();
		}
	}

	/**
	 * Converts a direction into a command
	 */
	public static Command fromDirection(Direction dir) {
		switch (dir) {
		case Y_POS:
			return Command.Y_POS;
		case X_POS:
			return Command.X_POS;
		case Y_NEG:
			return Command.Y_NEG;
		case X_NEG:
			return Command.X_NEG;
		default:
			return Command.WAIT;
		}
	}

	public static Command pickUp(int _quantity, float _weight) {
		Command com = Command.PICK;
		com.quantity = _quantity;
		com.weight = _weight;
		return com;
	}

	public void setX(int _x) {
		this.x = _x;
	}

	public void setY(int _y) {
		this.y = _y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getQuantity() {
		return quantity;
	}

	public float getWeight() {
		return weight;
	}

	public static ArrayDeque<Command> fromDirections(ArrayList<Direction> route) {
		ArrayDeque<Command> commands = new ArrayDeque<>(route.size());
		for (Direction d : route) {
			commands.add(Command.fromDirection(d));
		}
		return commands;
	}

	public void setFrom(int _x, int _y) {
		switch (this) {
		case Y_POS:
			x = _x;
			y = _y + 1;
			break;
		case Y_NEG:
			x = _x;
			y = _y - 1;
			break;
		case X_POS:
			x = _x + 1;
			y = _y;
			break;
		case X_NEG:
			x = _x - 1;
			y = _y;
			break;
		default:
			x = _x;
			y = _y;
			break;
		}
	}
}
