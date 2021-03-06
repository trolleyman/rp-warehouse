package warehouse.nxt.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.util.Delay;
import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.main.NXTMain;
import warehouse.nxt.motion.NXTMotion;
import warehouse.nxt.utils.MyString;
import warehouse.nxt.utils.Robot;

public class NXTCommunication implements Runnable {
	
	private DataOutputStream toPC;
	private DataInputStream  fromPC;
	private BTConnection     connection;
	
	private Robot me;
	
	private NXTMotion motion;
	private NXTInterface in;
	
	public NXTCommunication(NXTInterface _in) throws BluetoothStateException {
		this.in = _in;
		this.in.drawWaitForConnection(false);
		this.connection = Bluetooth.waitForConnection();
		this.toPC = connection.openDataOutputStream();
		this.fromPC = connection.openDataInputStream();
		getMyself();
		this.in.drawWaitForConnection(true);
		Sound.beepSequenceUp();
		Delay.msDelay(1000);
		
		String name = LocalDevice.getLocalDevice().getFriendlyName();
		this.motion = new NXTMotion(in, name);
		this.in.setRobotName(name);
		in.show();
	}
	
	// Get Robot information from the PC
	// Usage: Robot: <String: Name> <int: x> <int: y> <String: JobName>
	private void getMyself() {
		String input;
		
		try {
			input = this.fromPC.readUTF();
			
			String[] explosion = MyString.split(":", input);
			
			if (!explosion[0].equals("Robot")) {
				in.errorMenu("Protocol Error: Robot needs to be initialized.");
			} else {
				String[] data = MyString.split(",", explosion[1]);
				this.me = new Robot(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]));
				this.me.jobName = data[3];
			}
		} catch (IOException e) {
			in.errorMenu("Server closed.");
		}
	}

	@Override
	public void run() {
		try {
			this.in.updatePosition(this.me.x, this.me.y);
			this.in.show();
			sendReady();
			while (true) {
				String fromServer = this.fromPC.readUTF();
				// System.out.println(fromServer);
				this.find(fromServer);
				in.show();
			}
		} catch(IOException e) {
			in.errorMenu("Server closed.");
		}
	}
	
	// Categorises commands into Go or Do
	private void find(String _action) throws IOException {
		String[] explosion = MyString.split(":", _action);
		String type = explosion[0];

		String[] data = MyString.split(",", explosion[1]);
		switch (type) {
		case "Do":
			this.action(data);
			sendReady();
			break;
		case "Go":
			this.move(data);
			sendReady();
			sendDistance();
			break;
		case "Cancel Job":
			this.cancel(data);
			break;
		default:
			in.errorMenu("Protocol Error 1: (" + type + ")");
			break;
		}
	}

	private void sendReady() throws IOException {
		toPC.writeUTF("ready");
		toPC.flush();
	}
	
	private void sendDistance() throws IOException {
		toPC.writeUTF("Distance:" + motion.getDistance());
		toPC.flush();
	}

	// Executes a cancel request
	// Usage: "Cancel Job: Shut Down"
	// OR: "Cancel Job: <String: NextJobName>"
	private void cancel(String[] _next) {
		if (_next[0].equals("Shut Down")) {
			this.connection.close();
			in.errorMenu("Shut Down");
		} else {
			this.in.setJobName(_next[0]);
			this.in.show();
		}
	}

	// Executes doable actions like "Shut Down" and "Pick Up"
	// Usage: Do: Shut Down
	// Do: Pick Up, <int: quantity>, <double: weight>
	// Do: Drop Off
	private void action(String[] _action) {
		switch (_action[0]) {
		case "Shut Down":
			this.connection.close();
			in.errorMenu("Shut Down.");
			break;
		case "Pick Up":
			in.pickUp(Integer.parseInt(_action[1]), Float.parseFloat(_action[2]));
			break;
		case "Drop Off":
			in.dropOff();
			break;
		default:
			in.errorMenu("Protocol Error 2: Unknown data format received after 'Do: '");
			break;
		}
	}

	// Executes goable actions like "Go: Right" and "Go: Forward"
	// Usage: Go: Forward, <int: x>, <int: y>
	// Go: Backward, <int: x>, <int: y>
	// Go: Left, <int: x>, <int: y>
	// Go: Right, <int: x>, <int:y>
	private void move(String[] _data) {
		switch (_data[0]) {
		case "Right":
			this.in.directionUpdate("Right");
			this.motion.go("Right", Integer.parseInt(_data[1]), Integer.parseInt(_data[2]));
			break;
		case "Left":
			this.in.directionUpdate("Left");
			this.motion.go("Left", Integer.parseInt(_data[1]), Integer.parseInt(_data[2]));
			break;
		case "Forward":
			this.in.directionUpdate("Forward");
			this.motion.go("Forward", Integer.parseInt(_data[1]), Integer.parseInt(_data[2]));
			break;
		case "Backward":
			this.in.directionUpdate("Backward");
			this.motion.go("Backward", Integer.parseInt(_data[1]), Integer.parseInt(_data[2]));
			break;
		default:
			in.errorMenu("Protocol Error 3: Unknown data format received after 'Go: '");
			break;
		}
	}
}
