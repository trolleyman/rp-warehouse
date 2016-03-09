package warehouse.pc.bluetooth.testing;

import java.io.PrintStream;

public class DebugPrintStream {

	public static void enable() {
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
}
