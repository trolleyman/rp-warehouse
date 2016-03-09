package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.bluetooth.MessageListener;

public class SingleConnectionTest implements MessageListener {

	private final String name = "Dobot";
	private final String address = "0016530FD7F4";
	private BTServer server;
	private boolean reply;

	@Before
	public void setUp() throws Exception {
		// Enable custom print stream
		DebugPrintStream.enable();

		server = new BTServer();
		reply = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(server.open(new NXTInfo(BTServer.btProtocol, name, address)));
		server.addListener(this);
		server.sendToRobot(name, "check");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(reply);
		assertTrue(reply);
	}

	@Override
	public void newMessage(String robotName, String message) {
		System.out.println("Got reply " + robotName + " " + message);
		reply = true;
		System.out.println(reply);
	}
}
