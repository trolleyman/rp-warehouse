package warehouse.nxt.communication;

import java.io.DataOutputStream;
import java.io.IOException;

import warehouse.nxt.display.NXTInterface;
import warehouse.nxt.main.NXTMain;
import warehouse.nxt.motion.NXTMotion;
import warehouse.nxt.utils.Robot;

/**
 * 
 * Type: Class
 * Name: NXTSender
 * Author: Denis Makula
 * Description: Class that handles the data that must be sent to the PC, continuously sends the distance from Ultrasonic Sensor
 *              and sends whenever the status of the robot changes ( this will happen when the robot starts moving and
 *              when it stops. )
 * 
 **/

public class NXTSender extends Thread {

	private DataOutputStream toPC; // Output Stream to PC
	private Robot myself; // Robot Object Instance for this robot
	private Robot myself_old; // new Instance of the initial Robot Object which will not be changed
								// anywhere but this class
	private NXTMotion robotMotion; // Robot Motion Object which handles movements and Robot side actions
	private NXTInterface robotInterface; // Robot Interface Object which handles display

	private boolean stateChange;
	private boolean statusChange;
	private boolean positionChange;

	public NXTSender(DataOutputStream _toPC, Robot _myself, NXTMotion _rMotion,
		NXTInterface _rInterface) {
		this.toPC = _toPC;
		this.myself = _myself;
		this.myself_old = new Robot(_myself);
		this.robotMotion = _rMotion;
		this.robotInterface = _rInterface;
	}

	@Override
	public void run() {

		try {
			this.toPC.writeUTF("ready");
			this.toPC.flush();
			while (true) {

				this.updateCheck();

				if (this.stateChange) {
					this.toPC.writeUTF("ready");
					this.toPC.writeUTF("Distance:" + this.robotMotion.getDistance());
					this.toPC.flush();
					this.myself.ready = false;
					this.stateChange = false;
				}
				if (this.statusChange) {
					this.toPC.writeUTF(this.myself.status);
					this.toPC.flush();
					this.statusChange = false;
				}
				if (this.positionChange) {
					this.robotInterface.updatePosition(this.myself.x, this.myself.y);
					this.positionChange = false;
				}
			}
		} catch (IOException _exception) {
			NXTMain.error("Server closed.");
		}

	}

	// Checks for important updates of states between the old robot and the current one
	private void updateCheck() {
		if (!myself.equals(myself_old.status)) {
			this.statusChange = true;
		}
		if ((myself.x != myself_old.x) || (myself.y != myself_old.y)) {
			this.positionChange = true;
		}
		if ((myself.ready) && (!myself_old.ready)) {
			this.stateChange = true;
		}

		this.myself_old = new Robot(this.myself);
	}
}
