package warehouse.nxt.motion.behaviours;

import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.PathProvider;
import warehouse.nxt.utils.Robot;



public class WaitingBehaviour implements Behavior {
	private PathProvider provider;
	private volatile boolean shouldTakeControl;
	private Robot myself;
	
	public WaitingBehaviour( PathProvider _provider, Robot _robot ) {
		this.provider = _provider;
		this.shouldTakeControl = false;
		this.myself = _robot;
	}
		
	@Override
	public boolean takeControl() {
		if( this.shouldTakeControl ) { return true; }
		
		if( this.provider.isFinished() ) {
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
