package warehouse.nxt.motion.behaviours;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;
import warehouse.nxt.utils.Robot;
import warehouse.shared.Constants;



public class JunctionBehaviour implements Behavior {
	private static final double THRESHOLD = 62.0;
	private static final double TURNING_OFFSET = 0.07;
	
	private LightSensorCalibration calibration;
	private volatile DifferentialPilot pilot;
	private PathProvider provider;
	private volatile boolean shouldTakeControl;
	
	private Robot myself;
	
	public JunctionBehaviour( DifferentialPilot _pilot, LightSensorCalibration _calibration, PathProvider _provider, Robot _robot) {
		this.pilot = _pilot;
		this.calibration = _calibration;
		this.provider = _provider;
		this.shouldTakeControl = false;
		this.myself = _robot;
	}
	
	private boolean leftOnLine() {
		return this.calibration.leftSensor.readValue() < THRESHOLD;
	}
	
	private boolean rightOnLine() {
		return this.calibration.rightSensor.readValue() < THRESHOLD;
	}
	
	@Override
	public boolean takeControl() {
		if( this.shouldTakeControl ) { return true; }
		
		if( this.leftOnLine() && this.rightOnLine() && !this.provider.isFinished() ) {
			Sound.beep();
			this.shouldTakeControl = true;
		}
		
		return this.shouldTakeControl;
	}

	@Override
	public void action() {
		String direction = this.provider.getNextDirection();
		this.myself.status = "Moving " + direction;
		
		pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		//System.out.println("Jct:" + direction);
		
		if (direction == null)
			return;
		
		switch( direction ) {
			case "Backward": this.rotate( "back" ); break;
			case "Left"    : pilot.travel( TURNING_OFFSET ); this.rotate( "left" ); break;
			case "Right"   : pilot.travel( TURNING_OFFSET ); this.rotate( "right" ); break;
			case "Forward" : pilot.travel( TURNING_OFFSET ); break;
			default        : return;
		}
		
		this.shouldTakeControl = false;
	}

	@Override
	public void suppress() {  }
		
	private void rotate( String _direction ) {
		
		boolean right = false, left = false, back = false, rotating = false, decreased = false;
		int counter = 0;
		
		switch( _direction ) {
			case "left" : left = true; right  = false; back = false; counter = 1; break;
			case "right" : left = false; right = true; back = false; counter = 1; break;
			case "back" : left = false; right = false; back = true; counter = 2; break;
			default : break;
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
		rotating = false;
		
	}

}
