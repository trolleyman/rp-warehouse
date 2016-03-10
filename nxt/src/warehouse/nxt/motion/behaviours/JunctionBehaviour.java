package warehouse.nxt.motion.behaviours;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;



public class JunctionBehaviour implements Behavior {
	private static final double THRESHOLD = 65.0;
	private static final double TURNING_OFFSET = 0.07;
	
	private LightSensorCalibration calibration;
	private volatile DifferentialPilot pilot;
	private PathProvider provider;
	private volatile boolean shouldTakeControl;
	
	public JunctionBehaviour( DifferentialPilot _pilot, LightSensorCalibration _calibration, PathProvider _provider) {
		this.pilot = _pilot;
		this.calibration = _calibration;
		this.provider = _provider;
		this.shouldTakeControl = false;
	}
	
	private boolean leftOnLine() {
		return this.calibration.leftSensor .readValue() < THRESHOLD;
	}
	
	private boolean rightOnLine() {
		return this.calibration.rightSensor.readValue() < THRESHOLD;
	}
	
	@Override
	public boolean takeControl() {
		if( this.shouldTakeControl ) { return true; }
		
		if( this.leftOnLine() && this.rightOnLine() ) {
			Sound.beep();
			this.shouldTakeControl = true;
		}
		
		return this.shouldTakeControl;
	}

	@Override
	public void action() {
		String direction = this.provider.getNextDirection();
		pilot.setRotateSpeed(70.0);
		//System.out.println("Jct:" + direction);
		
		if (direction == null)
			return;
		
		switch( direction ) {
			case "Backward": pilot.rotate( 180 ); break;
			case "Left"    : pilot.travel( TURNING_OFFSET ); pilot.rotate( 90 ); break;
			case "Right"   : pilot.travel( TURNING_OFFSET ); pilot.rotate( -90 ); break;
			case "Forward" : pilot.travel( 0.05 ); break;
			default        : return;
		}
		
		shouldTakeControl = false;
	}

	@Override
	public void suppress() {  }
		
}
