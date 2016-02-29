package warehouse.pc.bluetooth;

import java.util.Arrays;
import java.util.LinkedList;

import lejos.pc.comm.NXTInfo;

public class TestOpenServer implements MessageListener, Runnable {
	
	private BTServer server;

	public static void main(String[] args) {
		new TestOpenServer().run();
	}

	@Override
	public void run() {
		server = new BTServer();
		if (server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"))) {
		}
		server.addListener(this);
		server.sendCommands("Dobot", new LinkedList<String>(Arrays.asList("left", "right", "forward")));
	}

	@Override
	public void newMessage(String robotName, String message) {
		System.out.println(robotName + " sent: " + message);
	}
}
