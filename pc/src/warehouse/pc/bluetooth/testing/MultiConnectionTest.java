package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.bluetooth.MessageListener;

public class MultiConnectionTest implements MessageListener {

	private HashMap<String, String> robots;
	private BTServer server;
	private int replies;
	
	@Before
	public void setUp() throws Exception {
		// Enable custom print stream
		DebugPrintStream.enable();
		
		// Add robots to the test
		robots = new HashMap<>();
		robots.put("Dobot", "0016530FD7F4");
		//robots.put("Vader", "0016531B5A19");
		robots.put("Jeff", "00165317BE35");
		
		server = new BTServer();
		replies = 0; 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for (Entry<String, String> e : robots.entrySet()) {
			assertTrue(server.open(new NXTInfo(BTServer.btProtocol, e.getKey(), e.getValue())));
		}
		
		server.addListener(this);
		
		for (Entry<String, String> e : robots.entrySet()) {
			server.sendToRobot(e.getKey(), "check");
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(replies, robots.size());
	}

	@Override
	public void newMessage(String robotName, String message) {
		replies++;
	}

}
