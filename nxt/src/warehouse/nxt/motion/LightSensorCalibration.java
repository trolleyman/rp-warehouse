package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

public class LightSensorCalibration {
	public int lLight, rLight, mLight;
	public int lDark, rDark, mDark;
	
	public LightSensor leftSensor;
	public LightSensor rightSensor;
	public LightSensor middleSensor;
	
	public LightSensorCalibration( LightSensor _leftSensor, LightSensor _rightSensor, LightSensor _middleSensor ) {
		this.leftSensor = _leftSensor;
		this.rightSensor = _rightSensor;
		this.middleSensor = _middleSensor;
		
		this.leftSensor.setFloodlight( true );
		this.rightSensor.setFloodlight( true );
		this.middleSensor.setFloodlight( true );
		
		System.out.println( "Place me on a light area" );
		Button.waitForAnyPress();
		this.lLight = this.leftSensor.readNormalizedValue();
		this.rLight = this.rightSensor.readNormalizedValue();
		this.mLight = this.middleSensor.readNormalizedValue();
		
		System.out.println( "Place me on a dark area" );
		Button.waitForAnyPress();
		this.lDark = this.leftSensor.readNormalizedValue();
		this.rDark = this.rightSensor.readNormalizedValue();
		this.mDark = this.middleSensor.readNormalizedValue();
	}
}
