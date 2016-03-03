package warehouse.pc.bluetooth.testing;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class MultiConnectionTest implements Runnable {
	
	private final String name1 = "Dobot";
	private final String address1 = "0016530FD7F4";
	private final String name2 = "Vader";
	private final String address2 = "0016531B5A19";
	
	private BTServer server;

	public static void main(String[] args) {
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
		
		new MultiConnectionTest().run();
	}

	@Override
	public void run() {
		server = new BTServer();
		if (server.open(new NXTInfo(BTServer.btProtocol, name2, address2))) {
			//server.sendCommands(name2, new LinkedList<String>(Arrays.asList("right", "right", "forward", "left")));
		}
		if (server.open(new NXTInfo(BTServer.btProtocol, name1, address1))) {
			server.sendCommands(name1, new LinkedList<String>(Arrays.asList("left", "right", "forward")));
		}
	}
}
