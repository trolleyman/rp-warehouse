package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class TestMultipleRobots {

	public static void main(String[] args) {
		// Enable custom print stream
		Debug.enableStream();
		
		// Add robots to the test
		HashMap<String, String> robots = new HashMap<>();
		robots.put("Jeff", "00165317BE35");
		robots.put("Dobot", "0016530FD7F4");
		// robots.put("Vader", "0016531B5A19");
		
		BTServer server = new BTServer();

		// Connect to all robots
		for (Entry<String, String> e : robots.entrySet()) {
			server.open(new NXTInfo(BTServer.btProtocol, e.getKey(), e.getValue()));
		}
		
		// Give them a list of commands
		for (Entry<String, String> e : robots.entrySet()) {
			server.sendCommands(e.getKey(), new LinkedList<>(Arrays.asList("right", "forward", "left")));
		}
	}
}
