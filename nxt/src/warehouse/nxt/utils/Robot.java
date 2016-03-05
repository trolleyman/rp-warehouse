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
	public double x;		// Robot X Position
	public double y;		// Robot Y Position
	public String status;	// Robot Status
	
	public Robot( String _name, double _x, double _y ) {
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
