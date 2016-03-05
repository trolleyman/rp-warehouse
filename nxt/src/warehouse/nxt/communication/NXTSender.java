package warehouse.nxt.communication;

import java.io.DataOutputStream;

import warehouse.nxt.utils.Robot;

public class NXTSender extends Thread {

	private DataOutputStream toPC;
	private Robot myself;
	private Robot myself_old;

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
				if( this.statusUpdated() ) { this.toPC.writeUTF( this.myself.status ); }
				if( this.positionUpdated() ) { /* NXTInterface.updatePosition( this.myself.x, this.myself.y ); */ }
			}
		}
		catch( Exception _exception ) { this.throwError( "NXTSender: Stream: Sending Failure." ); }
		
	}
	
	private boolean statusUpdated() {
		return ( !this.myself.status.equals( this.myself_old.status ) );
	}
	
	private boolean positionUpdated() {
		return ( ( this.myself.x != this.myself_old.x ) || ( this.myself.y != this.myself_old.y ) );
	}
	
	
	
	
	
	private void throwError( String _message ) { System.err.print( "\n" + _message ); System.exit( 0 ); }

}
