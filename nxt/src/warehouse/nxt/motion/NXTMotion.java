package warehouse.nxt.motion;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.behaviours.JunctionBehaviour;
import warehouse.nxt.motion.behaviours.TrackingBehaviour;
import warehouse.nxt.utils.DifferentialDriveRobot;
import warehouse.nxt.utils.Robot;
import warehouse.nxt.utils.WheeledRobotConfiguration;

public class NXTMotion {

	private Robot myself;
	private ArrayList<String> moves;
	private PathProvider provider;
	private Arbitrator arbitrator;

	public NXTMotion( Robot _myself ) {
		
		this.myself = _myself;
		this.moves = new ArrayList<String>();
		
		
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.111f, 0.111f, Motor.B, Motor.A );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		DifferentialPilot pilot = robot.getDifferentialPilot();

		this.provider = new SetPath( this.moves );
		
		LightSensorCalibration calibration = new LightSensorCalibration( new LightSensor( SensorPort.S1 ), new LightSensor( SensorPort.S2 ) );
		
		TrackingBehaviour tracking = new TrackingBehaviour( pilot, calibration, provider );
		JunctionBehaviour junction = new JunctionBehaviour( pilot, calibration, provider );
		
		this.arbitrator = new Arbitrator( new Behavior[] { tracking, junction }, true );

	}
	
	public void go( String _direction ) {
	
		this.moves.add( _direction );
		this.arbitrator.start();
		this.myself.status = "Idle";
		
	}
	
	public int getDistance() { return 0; }
	
}
