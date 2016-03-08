package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

public class LightSensorCalibration {
	public int lLight, rLight;
	public int lDark, rDark;
	
	public LightSensor leftSensor;
	public LightSensor rightSensor;
	
	public LightSensorCalibration( LightSensor leftSensor, LightSensor rightSensor ) {
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		
		leftSensor.setFloodlight( true );
		rightSensor.setFloodlight( true );
		
		System.out.println( "Place me on a light area" );
		Button.waitForAnyPress();
		lLight = leftSensor.readNormalizedValue();
		rLight = leftSensor.readNormalizedValue();
		
		System.out.println( "Place me on a dark area" );
		Button.waitForAnyPress();
		lDark = leftSensor.readNormalizedValue();
		rDark = leftSensor.readNormalizedValue();
	}
}
