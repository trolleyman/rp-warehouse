package warehouse.nxt.utils;


/**
 * 
 * Type: Class
 * Name: Robot
 * Author: Denis Makula
 * Description: Class that hold Robot information
 * 
 **/


public class Robot {

	public String name;		// Robot Name
	public int x;			// Robot X Position
	public int y;			// Robot Y Position
	public String status;	// Robot Status ( Possible: Idle, Moving, Picking Items, Picked <int>, Finished )
	
	public Robot( String _name, int _x, int _y ) {
		this.name = _name;
		this.x = _x;
		this.y = _y;
		this.status = "Idle";
	}

	public Robot( Robot _robot ) {
		this.name = _robot.name;
		this.x = _robot.x;
		this.y = _robot.y;
		this.status = _robot.status;
	}

}
