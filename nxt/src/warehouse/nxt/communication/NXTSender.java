package warehouse.nxt.communication;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.motion.NXTMotion;
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

	private DataOutputStream toPC;			// Output Stream to PC
	private Robot myself;					// Robot Object Instance for this robot
	private Robot myself_old;				// new Instance of the initial Robot Object which will not be changed anywhere but this class
	private NXTMotion robotMotion;			// Robot Motion Object which handles movements and Robot side actions
	private NXTInterface robotInterface;	// Robot Interface Object which handles display
	
	private boolean stateChange;
	private boolean statusChange;
	private boolean positionChange;

	public NXTSender( DataOutputStream _toPC, Robot _myself, NXTMotion _rMotion, NXTInterface _rInterface  ) {
		this.toPC = _toPC;
		this.myself = _myself;
		this.myself_old = new Robot( _myself );
		this.robotMotion = _rMotion;
		this.robotInterface = _rInterface;
	}
	
	@Override
	public void run() {
		
		try {
			while( true ) {
				
				this.updateCheck();
				
				if( this.stateChange ) { this.toPC.writeUTF( "Ready" ); this.toPC.writeUTF( "" + this.robotMotion.getDistance() ); this.myself.ready = false; this.stateChange = false; }
				if( this.statusChange ) { this.toPC.writeUTF( this.myself.status ); this.statusChange = false; }
				if( this.positionChange ) { this.robotInterface.updatePosition( this.myself.x, this.myself.y ); this.positionChange = false; }
				
			}
		}
		catch( IOException _exception ) { this.throwError( "NXTSender:" + _exception.getMessage() ); }
		
	}
	
	// Checks for important updates of states between the old robot and the current one
	private void updateCheck() {
		switch( this.myself.differentiate( this.myself_old ) ) {
			case "stateChange" : this.stateChange = true; break;
			case "statusChange" : this.statusChange = true; break;
			case "positionChange" : this.positionChange = true; break;
			default : break;
		}
		
		this.myself_old = new Robot( this.myself );
	}
		
	// Helper Method to throw errors
	private void throwError( String _message ) {
		System.err.print( "\n" + _message );
		Button.waitForAnyPress();
		System.exit( 0 );
	}

}
