package warehouse.nxt.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * The main class for the NXT. Creates data streams and receiving sending
 * threads.
 * 
 * @author Reece
 *
 */
public class NXTClient implements Runnable {
	
	private BTConnection connection;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private NXTReceiver receiver;
	private NXTSender sender;
	
	public NXTClient() {
		
	}

  @Override
	public void run() {
  	System.out.println("NXTClient");
  	System.out.println("Waiting for BT");
  	connection = Bluetooth.waitForConnection();
  	System.out.println("Got connection!");
  	
  	fromServer = connection.openDataInputStream();
  	toServer = connection.openDataOutputStream();
  	
  	receiver = new NXTReceiver(fromServer, this);
  	sender = new NXTSender(toServer);
  	
  	Thread rThread = new Thread(receiver);
  	rThread.start();
  	System.out.println("Started");
  	
  	// Wait for the receiver thread to end
  	try {
			rThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  	System.exit(0);
	}
  
  public void sendToServer(String message) {
  	sender.sendToServer(message);
  }
  
  public static void main(String[] args) {
  	new Thread(new NXTClient()).start();
  }
}
