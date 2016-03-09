package warehouse.shared;

import java.util.Optional;

/**
 * Commands the robot can execute in the field
 */

public enum Command {
	LEFT,
	RIGHT,
	FORWARD,
	BACKWARD,
	PICK,
	DROP,
	WAIT;
	
	private Integer x;
	private Integer y;
	private Integer quantity;
	private Float weight;
	
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
	
	public Optional<Integer> getX() {
		return Optional.ofNullable(x);
	}
	
	public Optional<Integer> getY() {
		return Optional.ofNullable(y);
	}
	
	public Optional<Integer> getQuantity() {
		return Optional.ofNullable(quantity);
	}
	
	public Optional<Float> getWeight() {
		return Optional.ofNullable(weight);
	}
}
