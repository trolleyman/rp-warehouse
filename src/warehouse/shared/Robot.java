package warehouse.shared;

public class Robot {
	private String name;
	private double xPos;
	private double yPos;
	private double facing;
	
	public Robot(String _name, double _xPos, double _yPos, double _facing) {
		this.name = _name;
		this.xPos = _xPos;
		this.yPos = _yPos;
		this.facing = _facing;
	}
	
	public String getName() {
		return name;
	}

	public double getX() {
		return xPos;
	}

	public void setX(double _xPos) {
		this.xPos = _xPos;
	}

	public double getY() {
		return yPos;
	}

	public void setY(double _yPos) {
		this.yPos = _yPos;
	}

	/**
	 * Gets the current angle of the robot, clockwise in degrees from Y+ vector
	 */
	public double getFacing() {
		return facing;
	}

	public void setFacing(double _facing) {
		this.facing = _facing;
	}
}
