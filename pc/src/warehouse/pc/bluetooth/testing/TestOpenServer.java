package warehouse.pc.bluetooth.testing;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

import lejos.pc.comm.NXTInfo;
import warehouse.pc.bluetooth.BTServer;

public class TestOpenServer implements Runnable {
	
	private final String name = "Dobot";
	private final String address = "0016530FD7F4";
	
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
