package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Connection {

	private DataInputStream fromRobot;
	private DataOutputStream toRobot;
	private String robotName;

	public Connection(String robotName, DataInputStream fromRobot, DataOutputStream toRobot) {
		this.robotName = robotName;
		this.fromRobot = fromRobot;
		this.toRobot = toRobot;
	}

	public void send(String command) {
		try {
			synchronized (BTServer.getLock()) {
				toRobot.writeUTF(command);
				toRobot.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String listen() {
		String reply = null;
		
		try {
			synchronized (BTServer.getLock()) {
				reply = fromRobot.readUTF();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return reply;
	}
}
