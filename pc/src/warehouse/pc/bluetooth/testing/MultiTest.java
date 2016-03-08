package warehouse.pc.bluetooth.testing;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class MultiTest {

	public static void main(String[] args) {
		BTServer server = new BTServer();
		
		server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"));
		server.open(new NXTInfo(BTServer.btProtocol, "Jeff", "00165317BE35"));
		
		server.sendToRobot("Dobot", "check");
		server.sendToRobot("Jeff", "check");
	}
}
