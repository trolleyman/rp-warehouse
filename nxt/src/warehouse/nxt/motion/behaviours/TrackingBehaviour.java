package warehouse.nxt.motion.behaviours;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.LightSensorCalibration;
import warehouse.nxt.motion.PathProvider;

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
		this.pilot.setTravelSpeed( 0.1 );
		
		Sound.setVolume( 100 );
		
		double calibrationError = ( this.calibration.rLight - this.calibration.lLight) / 2.0;
		 
		while( !stop ) {
			
			double left = this.calibration.leftSensor.readNormalizedValue() - calibrationError;
			double right = this.calibration.rightSensor.readNormalizedValue() + calibrationError;
			double error = right - left;
			final double k = 5.0;
			
			System.out.println("e:" + error);
			System.out.println("o:" + ( 1 / error ) * k);
			
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
