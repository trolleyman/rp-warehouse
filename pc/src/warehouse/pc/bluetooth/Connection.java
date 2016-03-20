package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTInfo;

public class Connection {

	private final DataInputStream fromRobot;
	private final DataOutputStream toRobot;
	private NXTInfo nxt;

	public Connection(NXTInfo nxt, DataInputStream fromRobot, DataOutputStream toRobot) {
		this.nxt = nxt;
		this.fromRobot = fromRobot;
		this.toRobot = toRobot;
	}
	
	public void close() {
		try {
			send("Do: Shut Down");
			this.toRobot.close();
			this.fromRobot.close();
		} catch (IOException e) {
			
		}
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

	public void send(String command) throws IOException {
		toRobot.writeUTF(command);
		toRobot.flush();
	}

	public String listen() throws IOException {
		return fromRobot.readUTF();
	}
}
