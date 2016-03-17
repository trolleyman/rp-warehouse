package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.util.Delay;
import warehouse.nxt.display.NXTInterface;

public class LightSensorCalibration {
	public int lLight, rLight;
	public int lDark, rDark;
	
	public LightSensor leftSensor;
	public LightSensor rightSensor;
	
	public LightSensorCalibration( NXTInterface in, LightSensor _leftSensor, LightSensor _rightSensor ) {
		this.leftSensor = _leftSensor;
		this.rightSensor = _rightSensor;
		
		this.leftSensor.setFloodlight( true );
		this.rightSensor.setFloodlight( true );
		 
		in.drawCalibrationPhase(true, false);
		Button.ENTER.waitForPress();
		this.lLight = this.leftSensor.readNormalizedValue();
		this.rLight = this.rightSensor.readNormalizedValue();
		this.leftSensor.calibrateHigh();
		this.rightSensor.calibrateHigh();
		in.drawCalibrationPhase(true, true);
		Delay.msDelay(1000);
		
		in.drawCalibrationPhase(false, false);
		Button.ENTER.waitForPress();
		this.lDark = this.leftSensor.readNormalizedValue();
		this.rDark = this.rightSensor.readNormalizedValue();
		this.leftSensor.calibrateLow();
		this.rightSensor.calibrateLow();
		in.drawCalibrationPhase(false, true);
		Delay.msDelay(1000);
	}
}
