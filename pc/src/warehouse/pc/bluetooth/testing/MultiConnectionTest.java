package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class MultiConnectionTest {

	private HashMap<String, String> robots;
	private BTServer server;

	@Before
	public void setUp() throws Exception {
		// Enable custom print stream
		Debug.enableStream();

		// Add robots to the test
		robots = new HashMap<>();
		robots.put("Jeff", "00165317BE35");
		robots.put("Dobot", "0016530FD7F4");
		// robots.put("Vader", "0016531B5A19");

		server = new BTServer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		// Connect to all robots
		for (Entry<String, String> e : robots.entrySet()) {
			assertTrue(server.open(new NXTInfo(BTServer.btProtocol, e.getKey(), e.getValue())));
		}
	}
}
