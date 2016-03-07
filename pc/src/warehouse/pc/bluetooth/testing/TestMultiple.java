package warehouse.pc.bluetooth.testing;

import java.util.HashMap;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.bluetooth.MessageListener;

public class TestMultiple implements Runnable, MessageListener {

	private HashMap<String, String> robots;
	private BTServer server;

	public static void main(String[] args) {
		// Enable custom print stream
		DebugPrintStream.enable();

		new TestMultiple().run();
	}

	@Override
	public void run() {
		server = new BTServer();

		// Add robots to the test
		robots = new HashMap<>();
		robots.put("Dobot", "0016530FD7F4");
		// robots.put("Vader", "0016531B5A19");
		robots.put("Jeff", "00165317BE35");
		
		server.open(new NXTInfo(BTServer.btProtocol, "Dobot", robots.get("Dobot")));
		server.open(new NXTInfo(BTServer.btProtocol, "Jeff", robots.get("Jeff")));
		
		server.addListener(this);
		
		server.sendToRobot("Dobot", "check");
		server.sendToRobot("Jeff", "check");
	}

	@Override
	public void newMessage(String robotName, String message) {
		System.out.println(robotName + " sent " + message);
	}
}
