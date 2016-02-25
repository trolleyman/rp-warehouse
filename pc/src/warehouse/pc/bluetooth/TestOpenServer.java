package warehouse.pc.bluetooth;

import lejos.pc.comm.NXTInfo;

public class TestOpenServer implements MessageListener, Runnable {

	public static void main(String[] args) {
		new TestOpenServer().run();
	}

	@Override
	public void run() {
		BTServer server = new BTServer();
		if (server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"))) {
			System.out.println("Saying hello");
			server.sendToRobot("Dobot", "Hello");
		}
		server.addListener("Dobot", this);
	}

	@Override
	public void newMessage(String robotName, String message) {
		System.out.println(robotName + " sent: " + message);
	}
}
