package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.util.Delay;
import warehouse.nxt.display.NXTInterface;

public class LightSensorCalibration {
	public int lLight, rLight, mLight;
	public int lDark, rDark, mDark;
	
	public LightSensor leftSensor;
	public LightSensor rightSensor;
	public LightSensor middleSensor;
	
	public LightSensorCalibration( NXTInterface in, LightSensor _leftSensor, LightSensor _rightSensor, LightSensor _middleSensor ) {
		this.leftSensor = _leftSensor;
		this.rightSensor = _rightSensor;
		this.middleSensor = _middleSensor;
		
		this.leftSensor.setFloodlight( true );
		this.rightSensor.setFloodlight( true );
		this.middleSensor.setFloodlight( true );
		 
		in.drawCalibrationPhase(true, false);
		Button.ENTER.waitForPress();
		this.lLight = this.leftSensor.readNormalizedValue();
		this.rLight = this.rightSensor.readNormalizedValue();
		this.mLight = this.middleSensor.readNormalizedValue();
		this.leftSensor.calibrateHigh();
		this.rightSensor.calibrateHigh();
		this.middleSensor.calibrateHigh();
		in.drawCalibrationPhase(true, true);
		Delay.msDelay(1000);
		
		in.drawCalibrationPhase(false, false);
		Button.ENTER.waitForPress();
		this.lDark = this.leftSensor.readNormalizedValue();
		this.rDark = this.rightSensor.readNormalizedValue();
		this.mDark = this.middleSensor.readNormalizedValue();
		this.leftSensor.calibrateLow();
		this.rightSensor.calibrateLow();
		this.middleSensor.calibrateLow();
		in.drawCalibrationPhase(false, true);
		Delay.msDelay(1000);
	}
}
