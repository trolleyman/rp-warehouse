package warehouse.nxt.communication;

import java.io.DataInputStream;

import lejos.nxt.comm.BTConnection;
import warehouse.nxt.utils.Robot;


/**
 * 
 * Type: Class
 * Name: NXTReceiver
 * Author: Denis Makula
 * Description: Class that is supposed to receive data from the PC, it will continuously try to get a String from the PC and
 *              everytime that happens, we execute the action and we update things around
 * 
 **/


public class NXTReceiver extends Thread {

	private DataInputStream fromPC;			// Input Stream from PC
	private BTConnection connection;		// Bluetooth Connection Wrapper
	private Robot myself;					// Robot Object Instance for this robot

	public NXTReceiver( DataInputStream _fromPC, BTConnection _connection, Robot _myself ) {
	
		this.fromPC = _fromPC;
		this.connection = _connection;
		this.myself = _myself;
		
	}
	
	public void run() {
		
		try {
			while( true ) { this.find( this.fromPC.readUTF() ); }
		}
		catch( Exception _exception ) { this.throwError( "NXTReceiver: Reading Failure." ); }
		
	}
	
	// Checks whether it needs to shut down or to execute an action ( lame but maybe good for later )
	private void find( String _action ) {
		if( _action.equals( "Shut Down" ) ) { this.connection.close(); System.exit( 0 ); }
		else { this.execute( _action ); }		
	}
	
	// Executes the action got from the PC Valid: "Forward, Backward, Left, Right"
	private void execute( String _action ) {
		// NXTInterface.updateDirection( _action );
		// NXTMotion.go( _action );
		this.myself.status = "Moving " + _action;
	}
	
	// Helper method to throw errors
	private void throwError( String _message ) { this.connection.close(); System.err.print( "\n" + _message ); System.exit( 0 ); }
	
}
