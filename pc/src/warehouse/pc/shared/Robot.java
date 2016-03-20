package warehouse.pc.shared;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class Robot implements Comparable<Robot> {
	public static final float MAX_WEIGHT = 50.0f;
	
	private RobotIdentity identity;
	// Current X Position of the Robot (horizontal axis)
	private double xPos;
	// Current Y Position of the Robot (vertical axis)
	private double yPos;
	// Which Direction the robot is facing clockwise in degrees relative to Y+
	private double facing;
	// Direction the robot is facing in a Direction
	private Direction direction;
	
	private int gridX;
	private int gridY;
	
	public Robot(String _name, String _id, double _xPos, double _yPos, double _facing) {
		this.identity = new RobotIdentity( _name, _id);
		this.xPos = _xPos;
		this.yPos = _yPos;
		this.facing = _facing;
		this.direction = Direction.fromFacing(facing);
		this.gridX = (int) xPos;
		this.gridY = (int) yPos;
	}
	/**
	 * Gets the robot's name
	 */
	public String getName() { return this.identity.name; }

	/**
	 * Gets the ID of the robot
	 */
	public String getID() { return this.identity.id; }
	
	/**
	 * Gets the current x position of the robot
	 */
	public double getX() { return this.xPos; }

	/**
	 * Gets the current y position of the robot
	 */
	public double getY() { return this.yPos; }
	
	/**
	 * Sets the current x position of the robot
	 */
	public void setX( double _xPos ) {
		this.xPos = _xPos;
		update();
	}
	
	/**
	 * Sets the current y position of the robot
	 */
	public void setY(double _yPos) {
		this.yPos = _yPos;
		update();
	}
	
	/**
	 * Gets the direction the robot is facing (one of the four Directions)
	 * @return the direction
	 */
	
	public Direction getDirection(){ return this.direction;}
	
	/**
	 * Gets the current direction the robot is facing in in degrees clockwise from the Y+ vector.
	 */
	public double getFacing() { return this.facing; }
	
	/**
	 * Sets the current direction of the robot.
	 */
	public void setFacing(double _facing) {
		this.facing = _facing;
		
		facing = facing % 360.0;
		if (facing < 0.0)
			facing = facing + 360.0;
		
		direction = Direction.fromFacing(facing);
		
		update();
	}
	
	public void setDirection(Direction _direction){
		this.direction = _direction;
		this.facing = direction.toFacing();
		
		update();
	}
	
	private void update() {
		MainInterface.get().updateRobot(this);
	}
	
	/**
	 * Robots are equal if their identities are equal.
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Robot && ((Robot) o).getIdentity().equals(this.getIdentity());
	}
	
	@Override
	public int hashCode() {
		return identity.hashCode();
	}
	
	@Override
	public String toString() {
		return identity.toString() + " @ [" + getX() + "," + getY() + "] facing:" + getFacing();
	}
	
	public RobotIdentity getIdentity() {
		return identity;
	}
	
	@Override
	public int compareTo(Robot other) {
		return this.getIdentity().compareTo(other.getIdentity());
	}

	public NXTInfo getNXTInfo() {
		return new NXTInfo(NXTCommFactory.BLUETOOTH, getName(), getID());
	}
	
	/**
	 * Clones the robot
	 */
	@Override
	public Robot clone() {
		return new Robot(identity.name, identity.id, xPos, yPos, facing);
	}
	
	public void setGridX(int _gridX) {
		this.gridX = _gridX;
	}
	public void setGridY(int _gridY) {
		this.gridY = _gridY;
	}
	
	/**
	 * Get the next grid x-coordinate that the robot is travelling to
	 */
	public int getGridX() {
		return gridX;
	}
	/**
	 * Get the next grid y-coordinate that the robot is travelling to
	 */
	public int getGridY() {
		return gridY;
	}
}
