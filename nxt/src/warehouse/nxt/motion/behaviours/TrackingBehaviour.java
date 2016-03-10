package warehouse.nxt.motion.behaviours;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;
import warehouse.shared.Constants;

public class TrackingBehaviour implements Behavior {

	private DifferentialPilot pilot;
	private LightSensorCalibration calibration;
	private volatile boolean stop = false;
	private volatile Thread thread;
	private PathProvider provider;

	public TrackingBehaviour( DifferentialPilot _pilot, LightSensorCalibration _calibration, PathProvider _provider ) {
		this.pilot = _pilot;
		this.calibration = _calibration;
		this.provider = _provider;
	}

	@Override
	public boolean takeControl() {
		if( provider.isFinished() ) { this.suppress(); return false; }
		else { return true; }
	}

	@Override
	public void action() {
		this.thread = Thread.currentThread();
		this.stop = false;
		this.pilot.setTravelSpeed(Constants.ROBOT_SPEED);
		this.pilot.setRotateSpeed(Constants.ROBOT_ROTATION_SPEED);
		
		Sound.setVolume( 100 );
		
		while( !stop ) {
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
		this.thread = null;

	}

	@Override
	public void suppress() {
		stop = true;
		if( this.thread != null) { this.thread.interrupt(); }
	}

}
