package warehouse.nxt.motion;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.DifferentialDriveRobot;
import warehouse.nxt.display.NXTInterface;
import warehouse.shared.Constants;
import warehouse.shared.RelativeDirection;

public class NXTMotion {
	private static final double TURNING_OFFSET = 0.07;
	
	// The higher the threshold, the more likely it is that the robot will detect a junction
	private static final double THRESHOLD_DEFAULT = 50.0;
	// The higher K is, the less sensitive the line follower will be
	private static final double K_DEFAULT = 5.0;
	
	private static final double THRESHOLD_BOT_LEE = 30.0;
	private static final double K_BOT_LEE = 3.0;
	
	private static final double THRESHOLD_DOBOT = 50.0;
	private static final double K_DOBOT = 3.0;
	
	private static final double THRESHOLD_VADER = 30.0;
	private static final double K_VADER = 5.0;
	
	private final double THRESHOLD;
	private final double K;
	
	private NXTInterface in;
	
	private DifferentialPilot pilot;
	private LightSensorCalibration calibration;
	
	private final UltrasonicSensor eyes;

	public NXTMotion( NXTInterface _in, String friendlyName ) {
		switch (friendlyName) {
		case "Bot Lee":
			THRESHOLD = THRESHOLD_BOT_LEE;
			K = K_BOT_LEE;
			break;
		case "Dobot":
			THRESHOLD = THRESHOLD_DOBOT;
			K = K_DOBOT;
			break;
		case "Vader":
			THRESHOLD = THRESHOLD_VADER;
			K = K_VADER;
			break;
		default:
			THRESHOLD = THRESHOLD_DEFAULT;
			K = K_DEFAULT;
		}
		
		this.in = _in;
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.118f, 0.111f, Motor.C, Motor.B );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		this.pilot = robot.getDifferentialPilot();
		LightSensor left =  new LightSensor( SensorPort.S3 );
		LightSensor right = new LightSensor( SensorPort.S1 );
		this.calibration = new LightSensorCalibration( friendlyName, in, left, right );
		
		this.eyes = new UltrasonicSensor( SensorPort.S4 );
	}
	
	/**
	 * Turns to a certain direction, then follows a line to the next direction and stops.
	 * @param _direction
	 * @param _x x coordinate this move will take the robot to
	 * @param _y y coordinate this move will take the robot to
	 */
	public void go( String _direction, int _x, int _y ) {
		//System.out.println("Go:" + _direction);
		turn(_direction);
		this.in.directionUpdate("Forward");
		trackToJunction();
		
		this.in.updatePosition(_x, _y);
	}
	
	/**
	 * Turns the robot in a direction
	 * @param dir the direction, one of "Backward", "Left", "Right", "Forward"
	 */
	private void turn(String dir) {
		this.pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		this.pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		
		switch( dir ) {
		case "Backward": this.rotate(RelativeDirection.BACKWARD); break;
		case "Left"    : this.rotate(RelativeDirection.LEFT); break;
		case "Right"   : this.rotate(RelativeDirection.RIGHT); break;
		case "Forward" : break;
		default        : return;
		}
	}
	
	/**
	 * Rotates the robot in a direction
	 * @param dir the direction
	 */
	private void rotate(RelativeDirection dir) {
		this.pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		this.pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		
		switch (dir) {
		case LEFT:     pilot.rotate(90.0);  break;
		case RIGHT:    pilot.rotate(-90.0); break;
		case BACKWARD: pilot.rotate(180.0); break;
		default:
		}
		if (true)
			return;
		
		boolean right = false, left = false, back = false, rotating = false, decreased = false;
		int counter = 0;
		
		switch( dir ) {
			case LEFT:
				left = true;
				counter = 1;
				break;
			case RIGHT:
				right = true;
				counter = 1;
				break;
			case BACKWARD:
				back = true;
				counter = 2;
				break;
			default:
				break;
		}
		
		while( right && !this.rightOnLine() ) {
			if( !rotating ) { this.pilot.rotateRight(); rotating = true; }
		}
		
		while( left && !this.leftOnLine() ) {
			if( !rotating ) { this.pilot.rotateLeft(); rotating = true; }
		}
		
		while( back && counter > 0 ) {
			while( this.leftOnLine() ) { if( !decreased ) { counter--; decreased = true; } }
			
			decreased = false;
			
			if( !rotating ) { this.pilot.rotateLeft(); rotating = true; }
		}
		
		this.pilot.stop();
	}
	
	private boolean rightOnLine() {
		return calibration.readRightValue() < THRESHOLD;
	}
	private boolean leftOnLine() {
		return calibration.readLeftValue() < THRESHOLD;
	}

	/**
	 * Follows a line to the next junction, then stops.
	 */
	private void trackToJunction() {
		this.pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		this.pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		
		while(!atJunction()) {
			double left = calibration.readLeftValue();
			double right = calibration.readRightValue();
			double error = right - left;
			
			//System.out.println("e:" + error);
			//System.out.println("o:" + ( 1 / error ) * k);
			
			this.pilot.travelArc( ( 1 / error ) * K, 0.1, true );
			
			try { Thread.sleep( 10 ); }
			catch (InterruptedException e) { /* Don't care. */ }
		}
		
		Sound.beep();
		
		pilot.travel(TURNING_OFFSET);
		
		this.pilot.stop();
	}
	
	/**
	 * Returns true if the robot sensors have detected a junction
	 */
	private boolean atJunction() {
		return leftOnLine() && rightOnLine();
	}

	public int getDistance() {
		int distance_one = this.eyes.getDistance();
		int distance_two = this.eyes.getDistance();
		
		while( ( distance_one - distance_two ) > THRESHOLD ) {
			distance_one = distance_two;
			distance_two = this.eyes.getDistance();
		}
		
		return ( int ) Math.ceil( ( distance_one + distance_two ) / 2 );
	}
	
}
