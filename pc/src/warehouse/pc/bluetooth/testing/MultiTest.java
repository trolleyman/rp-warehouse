package warehouse.pc.bluetooth.testing;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class MultiTest {

	public static void main(String[] args) {
		Debug.enableStream();
		
		BTServer server = new BTServer();
		
		server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"));
		server.open(new NXTInfo(BTServer.btProtocol, "Vader", "0016531B5A19"));
		
		Debug.waitForPress();
		
		server.sendToRobot("Dobot", "forward");
		server.sendToRobot("Vader", "right");
	}
}
