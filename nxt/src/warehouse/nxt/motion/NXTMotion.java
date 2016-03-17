package warehouse.nxt.motion;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.display.NXTInterface;
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
	
	private TrackingBehaviour trackingBehaviour;
	private JunctionBehaviour junctionBehaviour;
	
	private final UltrasonicSensor eyes;

	public NXTMotion( NXTInterface in, Robot _myself ) {
		
		this.myself = _myself;
		this.moves = new ArrayList<String>();

		this.provider = new SetPath(moves);
		
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.12f, 0.11f, Motor.C, Motor.B );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		DifferentialPilot pilot = robot.getDifferentialPilot();
		LightSensor left =  new LightSensor( SensorPort.S3 );
		LightSensor right = new LightSensor( SensorPort.S1 );
		LightSensorCalibration calibration = new LightSensorCalibration( in, left, right );
		
		this.eyes = new UltrasonicSensor( SensorPort.S4 );
		
		trackingBehaviour = new TrackingBehaviour( pilot, calibration, provider );
		junctionBehaviour = new JunctionBehaviour( pilot, calibration, provider );
	}
	
	public void go(String _direction, int _x, int _y) {
		//System.out.println("Go:" + _direction);
		this.moves.add(_direction);
		Arbitrator arby = new Arbitrator(new Behavior[] {trackingBehaviour, junctionBehaviour}, true);
		arby.start();
		
		this.myself.x = _x;
		this.myself.y = _y;
		this.myself.status = "Idle";
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
