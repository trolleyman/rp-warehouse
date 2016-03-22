package warehouse.nxt.motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import warehouse.nxt.display.NXTInterface;

public class LightSensorCalibration {
	private static final int BOT_LEE_LDARK  = (int)360;
	private static final int BOT_LEE_RDARK  = (int)390.9;
	private static final int BOT_LEE_LLIGHT = (int)513.7;
	private static final int BOT_LEE_RLIGHT = (int)490.1;
	
	private static final int DOBOT_LDARK  = (int)430.5;
	private static final int DOBOT_RDARK  = (int)384;
	private static final int DOBOT_LLIGHT = (int)556.7;
	private static final int DOBOT_RLIGHT = (int)464.8;
	
	private static final int VADER_LDARK  = (int)425.5;
	private static final int VADER_RDARK  = (int)394.9;
	private static final int VADER_LLIGHT = (int)550.8;
	private static final int VADER_RLIGHT = (int)526.8;
	
	private int lLight, rLight;
	private int lDark, rDark;
	
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	
	public LightSensorCalibration( String friendlyName, NXTInterface in, LightSensor _leftSensor, LightSensor _rightSensor ) {
		this.leftSensor = _leftSensor;
		this.rightSensor = _rightSensor;
		
		this.leftSensor.setFloodlight( true );
		this.rightSensor.setFloodlight( true );
		
		if (friendlyName.equals("Bot Lee")) {
			lDark  = BOT_LEE_LDARK;
			rDark  = BOT_LEE_RDARK;
			lLight = BOT_LEE_LLIGHT;
			rLight = BOT_LEE_RLIGHT;
			return;
		} else if (friendlyName.equals("Dobot")) {
			lDark  = DOBOT_LDARK;
			rDark  = DOBOT_RDARK;
			lLight = DOBOT_LLIGHT;
			rLight = DOBOT_RLIGHT;
			return;
		} else if (friendlyName.equals("Vader")) {
			lDark  = VADER_LDARK;
			rDark  = VADER_RDARK;
			lLight = VADER_LLIGHT;
			rLight = VADER_RLIGHT;
			return;
		}
		
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
			return (int)(((double)(l - lDark) / (double)(lLight - lDark)) * 100);
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
			return (int)(((double)(r - rDark) / (double)(rLight - rDark)) * 100);
		}
	}
}
