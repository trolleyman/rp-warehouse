package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class SingleSendingTest {

	private final String name = "Dobot";
	private final String address = "0016530FD7F4";
	private BTServer server;

	@Before
	public void setUp() throws Exception {
		// Enable custom print stream
		DebugPrintStream.enable();

		server = new BTServer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(server.open(new NXTInfo(BTServer.btProtocol, name, address)));

		// Send a check to robot
		server.sendToRobot(name, "check");

		// Check we get a response
		String reply = server.listen(name);
		System.out.println("Replied " + reply);
		assertEquals(reply, "ready");
	}
}