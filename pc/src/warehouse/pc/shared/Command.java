package warehouse.pc.shared;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Commands the robot can execute in the field
 */

public enum Command {
	Y_POS,
	X_POS,
	Y_NEG,
	X_NEG,
	PICK,
	DROP,
	COMPLETE_JOB,
	WAIT;
	
	private int x;
	private int y;
	private int quantity;
	private float weight;
	
	public Optional<Direction> toDirection() {
		switch (this) {
		case Y_POS: return Optional.of(Direction.Y_POS);
		case X_POS: return Optional.of(Direction.X_POS);
		case Y_NEG: return Optional.of(Direction.Y_NEG);
		case X_NEG: return Optional.of(Direction.X_NEG);
		default:    return Optional.empty();
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
			commands.add(d.toCommand());
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
