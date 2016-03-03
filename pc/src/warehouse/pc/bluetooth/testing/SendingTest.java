package warehouse.pc.bluetooth.testing;

import static org.junit.Assert.*;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;
import warehouse.pc.bluetooth.MessageListener;

public class SendingTest implements MessageListener {
	
	private final String name = "Dobot";
	private final String address = "0016530FD7F4";
	private BTServer server;
	private int replies;

	@Before
	public void setUp() throws Exception {
		// Custom System.out.println
    PrintStream stream = new PrintStream(System.out) {
      public void println(String s) {
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".")+1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        super.println("(" + className + "-" + methodName + " @ " + lineNumber + "): " + s); 
      }
    };
    System.setOut(stream);
		
		server = new BTServer();
		replies = 0;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(server.open(new NXTInfo(BTServer.btProtocol, name, address)));
		server.addListener(this);
		
		LinkedList<String> commands = new LinkedList<>(Arrays.asList("left", "right", "forward"));
		int size = commands.size();
		server.sendCommands(name, commands);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(replies + " " + (size + 1));
		assertTrue(replies == size + 1);
	}

	@Override
	public void newMessage(String robotName, String message) {
		if (robotName.equals(name) && message.equals("ready")) {
			System.out.println("Got reply");
			replies++;
		}
	}	
}
