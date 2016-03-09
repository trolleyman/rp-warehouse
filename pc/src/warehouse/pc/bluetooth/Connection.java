package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTInfo;

public class Connection implements Runnable {

	private final DataInputStream fromRobot;
	private final DataOutputStream toRobot;
	private NXTInfo nxt;

	public Connection(NXTInfo nxt, DataInputStream fromRobot, DataOutputStream toRobot) {
		this.nxt = nxt;
		this.fromRobot = fromRobot;
		this.toRobot = toRobot;
	}

	public boolean open(NXTComm comm) {
		try {
			comm.open(nxt);
		} catch (NXTCommException e) {
			e.printStackTrace();
		}

		// Make in and out streams
		//toRobot = new DataOutputStream(comm.getOutputStream());
		//fromRobot = new DataInputStream(comm.getInputStream());
		return true;
	}

	public void send(String command) {
		System.out.println("Sending " + command + " to " + nxt.name);
		try {
			toRobot.writeUTF(command);
			toRobot.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String listen() {
		String reply = null;
		System.out.println("Robot " + nxt.name + " is listening");

		try {
			reply = fromRobot.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return reply;
	}

	@Override
	public void run() {
		//open(comm);
		send("check");
	}
}
