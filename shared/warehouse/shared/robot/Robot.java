package warehouse.shared.robot;

import warehouse.shared.robot.Identity;

public class Robot {
	private Identity identity;		// Initialization of the Robot information
	private double xPos;			// Current Position of the Robot ( horizontal axis )
	private double yPos;			// Current Position of the Robot ( vertical axis )
	private double facing;			// Which Direction The robot is facing clockwise in degrees
	
	public Robot( String _name, String _ID, double _xPos, double _yPos, double _facing ) {
		this.identity = new Identity( _name, _ID, _xPos, _yPos );
		this.xPos = _xPos;
		this.yPos = _yPos;
		this.facing = _facing;
	}
	
	public String getName() { return this.identity.name; } // Gets the name of the Robot

	public String getID() { return this.identity.ID; } // Gets the ID of the Robot
	
	public double getX() { return this.xPos; } // Gets the current Position of the Robot ( horizontal axis )

	public double getY() { return this.yPos; } // Gets the current Position of the Robot ( vertical axis )

	public double getStartX() { return this.identity.xPos; } // Gets the starting Position of the Robot ( horizontal axis ) 
	
	public double getStartY() { return this.identity.yPos; } // Gets the starting Position of the Robot ( vertical axis ) 
	
	public void setX( double _xPos ) { this.xPos = _xPos; } // Sets the current Position of the Robot ( horizontal axis )

	public void setY(double _yPos) { this.yPos = _yPos; } // Sets the current Position of the Robot ( vertical axis )

	public double getFacing() { return this.facing; } // Gets the current angle of the robot, clockwise in degrees from Y+ vector

	public void setFacing(double _facing) { this.facing = _facing; } // Sets the current angle of the robot, clockwise in degrees from Y+ vector
	
}
