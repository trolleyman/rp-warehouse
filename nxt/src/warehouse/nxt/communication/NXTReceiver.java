package warehouse.nxt.communication;

import java.io.DataInputStream;

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
	
	public void run() {
		
		try { while( true ) { this.find( this.fromPC.readUTF() ); } }
		catch( Exception _exception ) { this.throwError( "NXTReceiver: Reading Failure." ); }
		
	}
	
	// Categorises commands into Go or Do
	private void find( String _action ) {
		
		String[] explosion = MyString.split( ":", _action );
		String type = explosion[ 0 ];
		
		String[] data = MyString.split( "," , explosion[ 1 ] );
		switch( type ) {
			case "Do" : this.action( data );break;
			case "Go" : this.move( explosion[ 1 ] );break;
			default : this.throwError( "NXTReceiver: Unknown data format received." ); break;
		}
	}
	
	// Executes doable actions like "Shut Down" and "Pick Up"
	// Usage: Do: Shut Down
	//        Do: Pick Up, <int: quantity>, <double: weight>
	//        Do: Drop Off
	private void action( String[] _action ) {
		switch( _action[ 0 ] ) {
			case "Shut Down" : this.connection.close(); System.exit( 0 ); break;
			case "Pick Up"	 : this.robotInterface.pickUp( Integer.parseInt( _action[ 1 ] ), Integer.parseInt( _action[ 2 ] ) ); this.myself.status = "Picking Items"; break;
			case "Drop Off"  : this.robotInterface.dropOff(); this.myself.status = "Finished"; break;
			default          : this.throwError( "NXTReceiver: Unknown data format received after 'Do: '." ); break;
		}
	}
	
	// Executes goable actions like "Go: Right" and "Go: Forward"
	// Usage: Go: Forward
	//        Go: Backward
	//        Go: Left
	//        Go: Right
	private void move( String _direction ) {
		switch( _direction ) {
		case "Right" 	: this.robotInterface.directionUpdate( "Right" ); this.myself.status = "Moving Right"; this.robotMotion.go( "Right" ); break;
		case "Left" 	: this.robotInterface.directionUpdate( "Left" ); this.myself.status = "Moving Left"; this.robotMotion.go( "Left" ); break;
		case "Forward" 	: this.robotInterface.directionUpdate( "Forward" ); this.myself.status = "Moving Forward"; this.robotMotion.go( "Forward" ); break;
		case "Backward" : this.robotInterface.directionUpdate( "Backward" ); this.myself.status = "Moving Backward"; this.robotMotion.go( "Backward" ); break;
		default 		: this.throwError( "NXTReceiver: Unknown data format received after 'Go: '." ); break;
		}
	}
	
	// Helper method to throw errors
	private void throwError( String _message ) { this.connection.close(); System.err.print( "\n" + _message ); System.exit( 0 ); }
	
}
