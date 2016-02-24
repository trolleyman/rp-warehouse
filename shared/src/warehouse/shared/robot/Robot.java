package warehouse.shared.robot;

import warehouse.shared.robot.Identity;

public class Robot {
	private Identity identity;		// Initialization of the Robot information
	private double xPos;			// Current Position of the Robot ( horizontal axis )
	private double yPos;			// Current Position of the Robot ( vertical axis )
	private double facing;			// Which Direction the robot is facing clockwise in degrees
	
	public Robot( String _name, String _ID, double _xPos, double _yPos, double _facing ) {
		this.identity = new Identity( _name, _ID, _xPos, _yPos );
		this.xPos = _xPos;
		this.yPos = _yPos;
		this.facing = _facing;
	}
	
	/**
	 * Gets the robot's name
	 */
	public String getName() { return this.identity.name; }

	/**
	 * Gets the ID of the robot
	 */
	public String getID() { return this.identity.ID; }
	
	/**
	 * Gets the current x position of the robot
	 */
	public double getX() { return this.xPos; }

	/**
	 * Gets the current y position of the robot
	 */
	public double getY() { return this.yPos; }

	/**
	 * Gets the initial x position of the robot
	 */
	public double getStartX() { return this.identity.xPos; }
	
	/**
	 * Gets the initial y position of the robot
	 */
	public double getStartY() { return this.identity.yPos; }
	
	/**
	 * Sets the current x position of the robot
	 */
	public void setX( double _xPos ) { this.xPos = _xPos; }
	
	/**
	 * Sets the current y position of the robot
	 */
	public void setY(double _yPos) { this.yPos = _yPos; }
	
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
		
	}
}
