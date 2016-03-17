package warehouse.nxt.motion;

import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.motion.behaviours.JunctionBehaviour;
import warehouse.nxt.motion.behaviours.TrackingBehaviour;
import warehouse.nxt.utils.DifferentialDriveRobot;
import warehouse.nxt.utils.WheeledRobotConfiguration;

public class Test {

	public static void main(String[] args) {
		ArrayList<String> moves = new ArrayList<>();
		PathProvider provider = new SetPath(moves);
		NXTInterface robotInterface = new NXTInterface( "", "None", 0.0f, 0, 0, 0 );
		
		WheeledRobotConfiguration config = new WheeledRobotConfiguration( 0.056f, 0.12f, 0.11f, Motor.C, Motor.B );
		DifferentialDriveRobot robot = new DifferentialDriveRobot( config );
		DifferentialPilot pilot = robot.getDifferentialPilot();
		LightSensor left =  new LightSensor( SensorPort.S3 );
		LightSensor right = new LightSensor( SensorPort.S1 );
		LightSensorCalibration calibration = new LightSensorCalibration( robotInterface, left, right );
		
		TrackingBehaviour trackingBehaviour = new TrackingBehaviour(pilot, calibration, provider);
		JunctionBehaviour junctionBehaviour = new JunctionBehaviour(pilot, calibration, provider);
		
		moves.add("Forward");
		moves.add("Left");
		moves.add("Right");
		moves.add("Forward");
		moves.add("Right");
		moves.add("Left");
		moves.add("Right");
		moves.add("Forward");
		moves.add("Forward");
		
		Button.waitForAnyPress();
	
		Arbitrator arby = new Arbitrator(new Behavior[] {trackingBehaviour, junctionBehaviour}, true);
		arby.start();
	}
}
