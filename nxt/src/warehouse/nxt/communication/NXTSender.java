package warehouse.nxt.communication;

import java.io.DataOutputStream;

import warehouse.nxt.utils.Robot;


/**
 * 
 * Type: Class
 * Name: NXTSender
 * Author: Denis Makula
 * Description: Class that handles the data that must be sent to the PC, continuously sends the distance from Ultrasonic Sensor
 *              and sends whenever the status of the robot changes ( this will happen when the robot starts moving and
 *              when it stops. )
 * 
 **/



public class NXTSender extends Thread {

	private DataOutputStream toPC;		// Output Stream to PC
	private Robot myself;				// Robot Object Instance for this robot
	private Robot myself_old;			// new Instance of the initial Robot Object which will not be changed anywhere but this class

	public NXTSender( DataOutputStream _toPC, Robot _myself ) {
		this.toPC = _toPC;
		this.myself = _myself;
		this.myself_old = new Robot( _myself );
	}
	
	public void run() {
		
		try {
			while( true ) {
				
				try { Thread.sleep( 100 ); }
				catch( Exception _exception ) { /* I guess we dont care */ }
				
				this.toPC.writeUTF( "Distance:"/* + NXTMotion.getDistance(); */ );
				
				if( this.statusUpdated() ) { this.toPC.writeUTF( this.myself.status ); this.updateOldRobot(); }
				if( this.positionUpdated() ) { /* NXTInterface.updatePosition( this.myself.x, this.myself.y ); */ this.updateOldRobot(); }
			
			}
		}
		catch( Exception _exception ) { this.throwError( "NXTSender: Stream: Sending Failure." ); }
		
	}
	
	// Checks if the Robot instance has changed by comparison to the initialized Instance
	private boolean statusUpdated() {
		return ( !this.myself.status.equals( this.myself_old.status ) );
	}
	
	// Same as statusUpdated but for the position
	private boolean positionUpdated() {
		return ( ( this.myself.x != this.myself_old.x ) || ( this.myself.y != this.myself_old.y ) );
	}
	
	// Resets the old robot as this one for later comparisons
	private void updateOldRobot() {
		this.myself_old = new Robot( this.myself );
	}
	
	
	// Helper Method to throw errors
	private void throwError( String _message ) { System.err.print( "\n" + _message ); System.exit( 0 ); }

}
