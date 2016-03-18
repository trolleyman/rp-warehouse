package warehouse.nxt.motion.behaviours;

import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;
import warehouse.nxt.utils.Robot;



public class WaitingBehaviour implements Behavior {

	private static final double THRESHOLD = 62.0;

	private PathProvider provider;
	private volatile boolean shouldTakeControl;
	private Robot myself;
	
	private LightSensorCalibration calibration;
	
	public WaitingBehaviour( LightSensorCalibration _calibration, PathProvider _provider, Robot _robot ) {
		this.provider = _provider;
		this.shouldTakeControl = false;
		this.myself = _robot;
		this.calibration = _calibration;
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
		
		if( this.leftOnLine() && this.rightOnLine() && this.provider.isFinished() ) {
			if( !this.myself.ready ) { this.myself.ready = true; this.myself.status = "Idle"; }
			this.shouldTakeControl = true;
		}
		
		return this.shouldTakeControl;
	}

	@Override
	public void action() {
		while( this.provider.isFinished() ) { this.shouldTakeControl = true; }
		shouldTakeControl = false;
	}

	@Override
	public void suppress() {  }
		
}
