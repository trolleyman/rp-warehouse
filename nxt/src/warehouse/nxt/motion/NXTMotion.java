package warehouse.nxt.motion;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.motion.behaviours.JunctionBehaviour;
import warehouse.nxt.motion.behaviours.TrackingBehaviour;
import warehouse.nxt.utils.DifferentialDriveRobot;
import warehouse.nxt.utils.Robot;
import warehouse.nxt.utils.WheeledRobotConfiguration;

public class NXTMotion {

	private static final double THRESHOLD = 7;

	private Robot myself;
	private ArrayList<String> moves;
	private PathProvider provider;
	private Arbitrator arbitrator;
	private final UltrasonicSensor eyes;

	public NXTMotion( Robot _myself ) {
		
		this.myself = _myself;
		this.moves = new ArrayList<String>();

		this.provider = new SetPath( this.moves );
		
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.111f, 0.111f, Motor.B, Motor.A );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		DifferentialPilot pilot = robot.getDifferentialPilot();
		LightSensor left =  new LightSensor( SensorPort.S1 );
		LightSensor right = new LightSensor( SensorPort.S2 );
		LightSensorCalibration calibration = new LightSensorCalibration( left, right );
		
		this.eyes = new UltrasonicSensor( SensorPort.S3 );
		
		TrackingBehaviour tracking = new TrackingBehaviour( pilot, calibration, provider );
		JunctionBehaviour junction = new JunctionBehaviour( pilot, calibration, provider );
		
		this.arbitrator = new Arbitrator( new Behavior[] { tracking, junction }, true );

	}
	
	public void go( String _direction ) {
	
		this.moves.add( _direction );
		this.arbitrator.start();
		
		switch( _direction ) {
			case "Left" 	: this.myself.x -= 1; break;
			case "Right" 	: this.myself.x += 1; break;
			case "Forward" 	: this.myself.y += 1; break;
			case "Backward" : this.myself.y -= 1; break;
			default 		: break;
		}
		
		this.myself.status = "Idle";
		
	}
	
	public int getDistance() {
		int distance_one = this.eyes.getDistance();
		int distance_two = this.eyes.getDistance();
		
		while( ( distance_one - distance_two ) > THRESHOLD ) { distance_one = distance_two; distance_two = this.eyes.getDistance(); }
		
		return ( int ) Math.ceil( ( distance_one + distance_two ) / 2 );
	}
	
}
