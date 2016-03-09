package warehouse.pc.bluetooth.testing;

import java.io.IOException;
import java.io.PrintStream;

public class Debug {

	public static void enableStream() {
		PrintStream stream = new PrintStream(System.out) {
			public void println(String s) {
				String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
				String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
				String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
				int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
				super.println("(" + className + "-" + methodName + " @ " + lineNumber + "): " + s);
			}
		};
		System.setOut(stream);
	}
	
	public static void waitForPress() {
		try {
			System.out.println("Press enter to continue...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
