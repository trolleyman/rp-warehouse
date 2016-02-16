package warehouse.shared;

public class Robot {
	private String name;
	private double xPos;
	private double yPos;
	private double facing;
	
	public Robot(String name, double xPos, double yPos, double facing) {
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
		this.facing = facing;
	}
	
	public String getName() {
		return name;
	}

	public double getX() {
		return xPos;
	}

	public void setX(double xPos) {
		this.xPos = xPos;
	}

	public double getY() {
		return yPos;
	}

	public void setY(double yPos) {
		this.yPos = yPos;
	}

	/**
	 * Gets the current angle of the robot, clockwise in degrees from Y+ vector
	 */
	public double getFacing() {
		return facing;
	}

	public void setFacing(double facing) {
		this.facing = facing;
	}
}
