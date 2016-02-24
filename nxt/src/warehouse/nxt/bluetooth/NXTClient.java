package warehouse.nxt.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * The main class for the NXT. Creates data streams and receiving sending
 * threads.
 * 
 * @author Reece
 *
 */
public class NXTClient {

	public static void main(String[] args) {

		System.out.println("NXTClient");
		System.out.println("Waiting for BT");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("Got connection!");

		System.out.println("Creating streams");
		DataInputStream fromServer = connection.openDataInputStream();
		DataOutputStream toServer = connection.openDataOutputStream();

		System.out.println("Creating threads");
		Thread receiver = new Thread(new NXTReceiver(fromServer));
		Thread sender = new Thread(new NXTSender(toServer));

		System.out.println("Starting threads");
		receiver.start();
		sender.start();

		System.out.println("Threads started");
		Button.waitForAnyPress();
		connection.close();
		System.exit(0);
	}

}
