package warehouse.pc.bluetooth.testing;

import java.util.Arrays;
import java.util.LinkedList;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class TestOpenServer implements Runnable {
	
	private final String name = "Dobot";
	private final String address = "0016530FD7F4";
	
	private BTServer server;

	public static void main(String[] args) {
		// Enable custom print stream
		DebugPrintStream.enable();
		
		new TestOpenServer().run();
	}

	@Override
	public void run() {
		server = new BTServer();
		if (server.open(new NXTInfo(BTServer.btProtocol, name, address))) {
			server.sendCommands(name, new LinkedList<String>(Arrays.asList("left", "right", "forward")));
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			server.sendCommands(name, new LinkedList<String>(Arrays.asList("forward", "forward", "right", "left")));
		}
	}
}
