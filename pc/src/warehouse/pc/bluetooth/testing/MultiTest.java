package warehouse.pc.bluetooth.testing;

import java.io.IOException;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class MultiTest {

	public static void main(String[] args) throws IOException {
		Debug.enableStream();
		
		BTServer server = new BTServer();
		
		server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"));
		server.open(new NXTInfo(BTServer.btProtocol, "Bot Lee", "001653155F9C"));
		
		Debug.waitForPress();
		
		server.sendToRobot("Dobot", "forward");
		server.sendToRobot("Bot Lee", "right");
		
		String reply2 = server.listen("Bot Lee");
		String reply1 = server.listen("Dobot");
		
		System.out.println(reply1 + " " + reply2);
	}
}
