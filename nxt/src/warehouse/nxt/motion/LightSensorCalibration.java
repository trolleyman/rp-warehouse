package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.util.Delay;
import warehouse.nxt.display.NXTInterface;

public class LightSensorCalibration {
	private int lLight, rLight;
	private int lDark, rDark;
	
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	
	public LightSensorCalibration( NXTInterface in, LightSensor _leftSensor, LightSensor _rightSensor ) {
		this.leftSensor = _leftSensor;
		this.rightSensor = _rightSensor;
		
		this.leftSensor.setFloodlight( true );
		this.rightSensor.setFloodlight( true );
		 
		in.drawCalibrationPhase(true, false);
		Button.ENTER.waitForPress();
		this.leftSensor.readNormalizedValue();
		this.rightSensor.readNormalizedValue();
		calibrateLightValue();
		Delay.msDelay(50);
		in.drawCalibrationPhase(true, true);
		Delay.msDelay(1000);
		
		in.drawCalibrationPhase(false, false);
		Button.ENTER.waitForPress();
		this.leftSensor.readNormalizedValue();
		this.rightSensor.readNormalizedValue();
		Delay.msDelay(50);
		calibrateDarkValue();
		
		this.lDark = this.leftSensor.readNormalizedValue();
		this.rDark = this.rightSensor.readNormalizedValue();
		this.leftSensor.calibrateLow();
		this.rightSensor.calibrateLow();
		in.drawCalibrationPhase(false, true);
		Delay.msDelay(1000);
	}
	
	private static final int SAMPLES = 4;
	
	private void calibrateLightValue() {
		this.lLight = 0;
		this.rLight = 0;
		for (int i = 0; i < SAMPLES; i++) {
			lLight += leftSensor.readNormalizedValue();
			rLight += rightSensor.readNormalizedValue();
			Delay.msDelay(50);
		}
		lLight /= SAMPLES;
		rLight /= SAMPLES;
	}
	
	private void calibrateDarkValue() {
		this.lDark = 0;
		this.rDark = 0;
		for (int i = 0; i < SAMPLES; i++) {
			lDark += leftSensor.readNormalizedValue();
			rDark += rightSensor.readNormalizedValue();
			Delay.msDelay(50);
		}
		lDark /= SAMPLES;
		rDark /= SAMPLES;
	}
	
	public int readLeftValue() {
		// Normalize reading to be between 0-100
		int l = leftSensor.readNormalizedValue();
		if (l < lDark) {
			return 0;
		} else if (l > lLight) {
			return 100;
		} else {
			return (l - lDark) / (lLight - lDark);
		}
	}
	
	public int readRightValue() {
		// Normalize reading to be between 0-100
		int r = rightSensor.readNormalizedValue();
		if (r < rDark) {
			return 0;
		} else if (r > rLight) {
			return 100;
		} else {
			return (r - rDark) / (rLight - rDark);
		}
	}
}
