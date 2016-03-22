package warehouse.nxt.main;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class Test {
	public static void main(String[] args) {
		while (true) {
			System.out.println(new LightSensor(SensorPort.S3).readNormalizedValue() + ":" + new LightSensor(SensorPort.S1).readNormalizedValue());
			Button.waitForAnyPress();
		}
	}
}
