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
import warehouse.nxt.utils.Robot;
import warehouse.shared.Constants;
import warehouse.shared.RelativeDirection;

public class NXTMotion {
	private static final double THRESHOLD = 62.0;
	private static final double TURNING_OFFSET = 0.07;
	
	private NXTInterface in;
	
	private DifferentialPilot pilot;
	private LightSensorCalibration calibration;
	
	private final UltrasonicSensor eyes;

	public NXTMotion( NXTInterface _in ) {
		this.in = _in;
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.111f, 0.111f, Motor.C, Motor.B );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		this.pilot = robot.getDifferentialPilot();
		LightSensor left =  new LightSensor( SensorPort.S3 );
		LightSensor right = new LightSensor( SensorPort.S1 );
		this.calibration = new LightSensorCalibration( in, left, right );
		
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
		
		trackToJunction();
		
		this.in.updatePosition(_x, _y);
	}
	
	/**
	 * Turns the robot in a direction
	 * @param dir the direction, one of "Backward", "Left", "Right", "Forward"
	 */
	private void turn(String dir) {
		switch( dir ) {
		case "Backward": this.rotate(RelativeDirection.BACKWARD); break;
		case "Left"    : pilot.travel(TURNING_OFFSET); this.rotate(RelativeDirection.LEFT); break;
		case "Right"   : pilot.travel(TURNING_OFFSET); this.rotate(RelativeDirection.RIGHT); break;
		case "Forward" : pilot.travel(TURNING_OFFSET); break;
		default        : return;
		}
	}
	
	/**
	 * Rotates the robot in a direction
	 * @param dir the direction
	 */
	private void rotate(RelativeDirection dir) {
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
		return this.calibration.rightSensor.readValue() < THRESHOLD;
	}
	private boolean leftOnLine() {
		return this.calibration.leftSensor .readValue() < THRESHOLD;
	}

	/**
	 * Follows a line to the next junction, then stops.
	 */
	private void trackToJunction() {
		this.pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		this.pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		
		Sound.setVolume( 100 );
		
		while(!atJunction()) {
			double left = this.calibration.leftSensor.getLightValue();
			double right = this.calibration.rightSensor.getLightValue();
			double error = right - left;
			final double k = 5.0;
			
			//System.out.println("e:" + error);
			//System.out.println("o:" + ( 1 / error ) * k);
			
			this.pilot.travelArc( ( 1 / error ) * k, 0.1, true );
			
			try { Thread.sleep( 10 ); }
			catch (InterruptedException e) { /* Don't care. */ }
		}
		
		this.pilot.stop();
	}
	
	/**
	 * Returns true if the robot sensors have detected a junction
	 * @return
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
