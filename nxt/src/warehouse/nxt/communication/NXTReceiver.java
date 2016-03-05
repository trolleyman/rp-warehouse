package warehouse.nxt.communication;

import java.io.DataInputStream;

import lejos.nxt.comm.BTConnection;
import warehouse.nxt.utils.Robot;

public class NXTReceiver extends Thread {

	private DataInputStream fromPC;
	private BTConnection connection;
	private Robot myself;

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
	
	private void find( String _action ) {
		if( _action.equals( "Shut Down" ) ) { this.connection.close(); System.exit( 0 ); }
		else { this.execute( _action ); }		
	}
	
	private void execute( String _action ) {
		// NXTInterface.updateDirection( _action );
		// NXTMotion.go( _action );
		this.myself.status = "Moving " + _action;
	}
	
	private void throwError( String _message ) { this.connection.close(); System.err.print( "\n" + _message ); System.exit( 0 ); }
	
}
