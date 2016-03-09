package warehouse.nxt.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.motion.NXTMotion;
import warehouse.nxt.utils.MyString;
import warehouse.nxt.utils.Robot;


/**
 * 
 * Type: Class
 * Name: NXTReceiver
 * Author: Denis Makula
 * Description: Class that is supposed to receive data from the PC, it will continuously try to get a String from the PC and
 *              every time that happens, we execute the action and we update things around
 * 
 *              Possible Receives:
 *              	Do: Shut Down
 *              	Do: Pick < int: Quantity > < int: Weight >
 *              	Do: Drop Off
 * 
 *              	Go: Forward
 *              	Go: Left
 *              	Go: Right
 *              	Go: Backward
 * 
 **/


public class NXTReceiver extends Thread {

	private DataInputStream fromPC;			// Input Stream from PC
	private BTConnection connection;		// Bluetooth Connection Wrapper
	private Robot myself;					// Robot Object Instance for this robot
	private NXTMotion robotMotion;			// Robot Motion Control Object
	private NXTInterface robotInterface;	// Robot Interface Control Object
	
	public NXTReceiver( DataInputStream _fromPC, BTConnection _connection, Robot _myself, NXTMotion _rMotion, NXTInterface _rInterface ) {
	
		this.fromPC = _fromPC;
		this.connection = _connection;
		this.myself = _myself;
		this.robotMotion = _rMotion;
		this.robotInterface = _rInterface;
		
	}
	
	@Override
	public void run() {
		
		try {
			while( true ) {
				String fromServer = this.fromPC.readUTF();
				System.out.println(fromServer);
				this.find( fromServer );
			}
		} catch( IOException _exception ) {
			this.throwError( "Recieve: " + _exception.getMessage() );
		}
	}
	
	// Categorises commands into Go or Do
	private void find( String _action ) {
		
		String[] explosion = MyString.split( ":", _action );
		String type = explosion[ 0 ];
		
		String[] data = MyString.split( "," , explosion[ 1 ] );
		switch( type ) {
			case "Do" : this.action( data );break;
			case "Go" : this.move( data );break;
			case "Cancel Job" : this.cancel( data );
			default : this.throwError( "NXTReceiver: Unknown data format received." ); break;
		}
	}
	
	// Executes a cancel request
	// Usage: "Cancel Job: Shut Down"
	//    OR: "Cancel Job: <String: NextJobName>"
	private void cancel( String[] _next ) {
		if( _next[ 0 ].equals( "Shut Down" ) ) { this.connection.close(); throwError("Shut Down"); }
		else {
			this.robotInterface.setJobName( _next[ 0 ] );
			this.myself.jobName = _next[ 0 ];
			this.robotInterface.show();
		}
	}
	
	// Executes doable actions like "Shut Down" and "Pick Up"
	// Usage: Do: Shut Down
	//        Do: Pick Up, <int: quantity>, <double: weight>
	//        Do: Drop Off
	private void action( String[] _action ) {
		switch( _action[ 0 ] ) {
			case "Shut Down"  : this.connection.close(); throwError("Shut Down"); break;
			case "Pick Up"	  : this.myself.status = "Picking Items"; this.robotInterface.pickUp( Integer.parseInt( _action[ 1 ] ), Integer.parseInt( _action[ 2 ] ) ); this.myself.status = "Picked " + Integer.parseInt( _action[ 1 ] ); break;
			case "Drop Off"   : this.robotInterface.dropOff(); this.myself.status = "Finished"; break;
			default           : this.throwError( "NXTReceiver: Unknown data format received after 'Do: '." ); break;
		}
	}
	
	// Executes goable actions like "Go: Right" and "Go: Forward"
	// Usage: Go: Forward, <int: x>, <int: y>
	//        Go: Backward, <int: x>, <int: y>
	//        Go: Left, <int: x>, <int: y>
	//        Go: Right, <int: x>, <int:y>
	private void move( String[] _data ) {
		switch( _data[0] ) {
		case "Right" 	:
			this.robotInterface.directionUpdate( "Right" );
			this.myself.status = "Moving Right";
			this.robotMotion.go( "Right", Integer.parseInt( _data[1] ), Integer.parseInt( _data[2] ) );
			break;
		case "Left" 	:
			this.robotInterface.directionUpdate( "Left" );
			this.myself.status = "Moving Left";
			this.robotMotion.go( "Left", Integer.parseInt( _data[1] ), Integer.parseInt( _data[2] ) );
			break;
		case "Forward" 	:
			this.robotInterface.directionUpdate( "Forward" );
			this.myself.status = "Moving Forward";
			this.robotMotion.go( "Forward", Integer.parseInt( _data[1] ), Integer.parseInt( _data[2] ) );
			break;
		case "Backward" :
			this.robotInterface.directionUpdate( "Backward" );
			this.myself.status = "Moving Backward";
			this.robotMotion.go( "Backward", Integer.parseInt( _data[1] ), Integer.parseInt( _data[2] ) );
			break;
		default 		: this.throwError( "NXTReceiver: Unknown data format received after 'Go: '." ); break;
		}
	}
	
	// Helper method to throw errors
	private void throwError( String _message ) {
		this.connection.close(); System.err.println( _message );
		Button.waitForAnyPress();
		System.exit( 0 );
	}
	
}
