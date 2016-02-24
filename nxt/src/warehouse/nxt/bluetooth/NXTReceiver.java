package warehouse.nxt.bluetooth;

import java.io.DataInputStream;

/**
 * The receiver thread for the NXT. Receives messages from the server the NXT is
 * connected to.
 * 
 * @author Reece
 *
 */
public class NXTReceiver implements Runnable {

	private DataInputStream fromServer;

	/**
	 * Create the NXTReceiver.
	 * 
	 * @param fromServer The DataInputStream from the server.
	 */
	public NXTReceiver(DataInputStream fromServer) {
		this.fromServer = fromServer;
	}
	
	@Override
	public void run() {
		System.out.println("Receiver running");
	}
}
