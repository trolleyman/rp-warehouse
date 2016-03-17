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
	public String jobName;	// The Name of the Job this robot is currently doing
	public String status;	// Robot Status ( Possible: Idle, Moving, Picking Items, Picked <int>, Finished )
	public boolean ready;
	
	public Robot( String _name, int _x, int _y ) {
		this.name = _name;
		this.x = _x;
		this.y = _y;
		this.jobName = "None";
		this.status = "Idle";
		this.ready = false;
	}

	public Robot( Robot _robot ) {
		this.name = _robot.name;
		this.x = _robot.x;
		this.y = _robot.y;
		this.jobName = _robot.jobName;
		this.status = _robot.status;
		this.ready = _robot.ready;
	}
	
	public String differentiate( Robot _robot ) {
		if( !this.status.equals( _robot.status ) ) { return "statusChange"; }
		else if( ( this.x != _robot.x ) || ( this.y != _robot.y ) ) { return "updateChange"; }
		else if( ( this.ready ) && ( !_robot.ready ) ) { return "stateChange"; }
		else { return "I'm the most beautiful man ever to be born on this planet."; }
	}

}
