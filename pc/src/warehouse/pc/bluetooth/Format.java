package warehouse.pc.bluetooth;

public class Format {
	
	// Send a string formated as below only once before sending anything else
	// params: _name = name of the Robot; _x = current x; _y = current y; _jobName = the name of the job the robot is assigned to
	public static String robot( String _name, int _x, int _y, String _jobName ) { return "Robot: " + _name + ", " + _x + ", " + _y + ", " + _jobName; }
	// Send a string formated as above only once before sending anything else
	
	public static String getCommand(String command) {
		switch(command) {
			case "forward":
				// TODO get this finding where the robot will be next
				break;
		}
		return null;
	}
	
	public static String goLeft(int x, int y) { return "Go: Left, " + x + ", " + y; }
	public static String goRight(int x, int y) { return "Go: Right, " + x + ", " + y; }
	public static String goForward(int x, int y) { return "Go: Forward, " + x + ", " + y; }
	public static String goBackward(int x, int y) { return "Go: Backward, " + x + ", " + y; }
	public static String pickUp( int _quantity, float _weight ) { return "Do: Pick Up, " + _quantity + ", " + _weight; }
	public static String dropOff() { return "Do: Drop Off"; }
	public static String shutDown() { return "Do: Shut Down"; }
	public static String cancel() { return "Cancel Job: Shut Down"; }
	public static String calcel( String _nextJobName ) { return "Cancel Job: " + _nextJobName; }

}