package warehouse.nxt.motion.behaviours;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;



public class JunctionBehaviour implements Behavior {
	private static final double THRESHOLD = 90.0;
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
		return ( Math.abs( this.calibration.leftSensor .readNormalizedValue() - this.calibration.lDark ) < THRESHOLD );
	}
	
	private boolean rightOnLine() {
		return ( Math.abs( this.calibration.rightSensor.readNormalizedValue() - this.calibration.rDark ) < THRESHOLD );
	}
	
	@Override
	public boolean takeControl() {
		if( this.shouldTakeControl ) { return true; }
		
		if( this.leftOnLine() && this.rightOnLine() ) { this.shouldTakeControl = true; }
		
		return this.shouldTakeControl;
	}

	@Override
	public void action() {
		try {
			String direction = this.provider.getNextDirection();
			if( direction == null ) { return; }
			
			switch( direction ) {
				case "Backward" : pilot.rotate( 180 ); break;
				case "Left"     : pilot.travel( TURNING_OFFSET ); pilot.rotate( 90 ); break;
				case "Right"    : pilot.travel( TURNING_OFFSET ); pilot.rotate( -90 ); break;
				case "Forward"  : pilot.travel( 0.05 ); break;
				default         : pilot.travel( 0.05 ); break;
			}
		}
		finally { shouldTakeControl = false; }
	}

	@Override
	public void suppress() {  }
		
}
