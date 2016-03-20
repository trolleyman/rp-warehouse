package warehouse.nxt.main;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import warehouse.nxt.communication.NXTCommunication;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.motion.NXTMotion;

/**
 * 
 * Type: Class
 * Name: NXTMain
 * Author: Denis Makula
 * Description: What will be runned on the Robot, this class is supposed to connect with the PC trigger 2 threads,
 *              one sends data to the PC one receives data from the PC. Both either check for changes inside Robot,
 *              or produce changes inside Robot so that the one which checks will be notified.
 * 
 **/

public class NXTMain {
	public NXTMain() {
		this.connect();
	}
	
	// Waits for a Connection, when one is succeeded, calls .startStreams and .startThreads
	private void connect() {
		NXTInterface in = new NXTInterface( "", 0, 0 );
		
		NXTCommunication comm = new NXTCommunication(in);
		comm.run();
	}
	
	public static void main(String[] _arguments) {
		new NXTMain();
	}
	
	public static void error(String msg) {
		Sound.systemSound(true, 4);
		LCD.clear();
		LCD.drawString(msg, 0, 0);
		Button.waitForAnyPress();
		System.exit(1);
	}
}
