package warehouse.pc.shared;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

import warehouse.shared.Direction;

/**
 * Commands the robot can execute in the field
 */

public class Command {
	private CommandType type;
	
	// Metadata
	private int x;
	private int y;
	private int quantity;
	private float weight;
	
	public Command(CommandType _type) {
		this.type = _type;
	}

	public Command(int _quantity, float _weight) {
		this.type = CommandType.PICK;
		this.quantity = _quantity;
		this.weight = _weight;
	}

	public Optional<Direction> toDirection() {
		switch (type) {
		case Y_POS: return Optional.of(Direction.Y_POS);
		case X_POS: return Optional.of(Direction.X_POS);
		case Y_NEG: return Optional.of(Direction.Y_NEG);
		case X_NEG: return Optional.of(Direction.X_NEG);
		default:    return Optional.empty();
		}
	}
	
	/**
	 * Converts a direction into a command
	 */
	public static Command fromDirection(Direction dir) {
		switch (dir) {
		case Y_POS: return new Command(CommandType.Y_POS);
		case X_POS: return new Command(CommandType.X_POS);
		case Y_NEG: return new Command(CommandType.Y_NEG);
		case X_NEG: return new Command(CommandType.X_NEG);
		default: return new Command(CommandType.WAIT);
		}
	}
	
	public CommandType getType() {
		return type;
	}
	
	public static Command pickUp(int _quantity, float _weight) {
		return new Command(_quantity, _weight);
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
		switch (type) {
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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Command) {
			Command c = (Command)o;
			return c.type == type
				&& c.weight == weight
				&& c.quantity == quantity
				&& c.x == x
				&& c.y == y;
		}
		return false;
	}
}
