package warehouse.nxt.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import warehouse.nxt.communication.NXTReceiver;
import warehouse.nxt.communication.NXTSender;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.motion.NXTMotion;
import warehouse.nxt.utils.MyString;
import warehouse.nxt.utils.Robot;


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

	private BTConnection connection;		// Bluetooth connection wrapper
	private DataInputStream fromPC;			// Input Stream from PC
	private DataOutputStream toPC;			// Output Stream to PC
	private Robot myself;					// Robot Object Instance for this robot
	
	private NXTReceiver receiver;			// Receiver thread
	private NXTSender sender;				// Sender thread
	
	private NXTMotion robotMotion;
	private NXTInterface robotInterface;
	
	public NXTMain() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread _t, Throwable _e) {
				System.err.println("Exception");
				_e.printStackTrace();
				Button.waitForAnyPress();
				System.exit(1);
			}
		});
		
		this.say( "Waiting for connection." );
		this.connect();

	}
	
	// Waits for a Connection, when one is succeeded, calls .startStreams and .startThreads
	private void connect() {
		this.connection = Bluetooth.waitForConnection();
		
		this.startStreams();
		this.getMyself();
		this.initRobotInterface();
		this.initRobotMotion();
		this.startThreads();
		
		this.robotInterface.show();
	}
	
	// Initialize Robot Interface
	private void initRobotInterface() {
		this.robotInterface = new NXTInterface( this.myself );
	}
	
	private void initRobotMotion() {
		this.robotMotion = new NXTMotion( this.myself );
	}
	
	// Initializes the I/O Streams
	private void startStreams() {
		this.fromPC = this.connection.openDataInputStream();
		this.toPC = this.connection.openDataOutputStream();
	}
	
	// Get Robot information from the PC
	// Usage: Robot: <String: Name> <int: x> <int: y> <String: JobName>
	private void getMyself() {
		String input;
		
		try {
			input = this.fromPC.readUTF();
			
			String[] explosion = MyString.split( ":", input );
			
			if( !explosion[ 0 ].equals( "Robot" ) ) { System.err.print( "\nRobot needs to be initialized before sending data to it." ); Button.waitForAnyPress(); System.exit(1); }
			else {
				String[] data = MyString.split( "," , explosion[ 1 ] );
				this.myself = new Robot(data[0], Integer.parseInt( data[ 1 ] ), Integer.parseInt( data[2] ));
				this.myself.jobName = data[3];
			}
			
			this.toPC.writeUTF("Idle");
		}
		catch( IOException _exception ) { _exception.printStackTrace(); }
	}
	
	// Starts the Sender and Receiver Threads
	private void startThreads() {
		this.sender = new NXTSender( this.toPC, this.myself, this.robotMotion, this.robotInterface );
		this.receiver = new NXTReceiver( this.fromPC, this.connection, this.myself, this.robotMotion, this.robotInterface );
		
		this.sender.start();
		this.receiver.start();
	}
	
	
	
	private void say( String _message ) { System.out.println( _message ); }	// Helper method to print
	public static void main( String[] _arguments ) { new NXTMain(); }
	
}