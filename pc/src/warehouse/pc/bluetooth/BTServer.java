package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * The BT communication "server". Can create new thread pairs for NXTs.
 * 
 * @author Reece
 *
 */
public class BTServer {

  public static int btProtocol;
  private NXTComm comm;

  /**
   * Setup the communication "server" for the current OS and driver.
   */
  public BTServer() {
    // Create the comms system for this OS and driver
    btProtocol = NXTCommFactory.BLUETOOTH;

    try {
      comm = NXTCommFactory.createNXTComm(btProtocol);
    } catch (NXTCommException e) {
      e.printStackTrace();
      System.err.println("Could not open the btCommunication");
    }
  }

  /**
   * Try to open a connection and threads to a NXT. First the in and output
   * streams are made, then passed to the sender and receiver which are started
   * in new threads.
   * 
   * @param nxt The protocol type, name and id of the NXT.
   * @return True if the connection was opened and false if not.
   */
  public boolean open(NXTInfo nxt) {
    try {

      System.out.println("Tring to open a connection");
      if (comm.open(nxt)) {
        System.out.println("Making stream and reader");
        DataOutputStream toRobot = new DataOutputStream(comm.getOutputStream());
        DataInputStream fromRobot = new DataInputStream(comm.getInputStream());

        System.out.println("Creating threads");
        Thread sender = new Thread(new ServerSender(toRobot));
        Thread receiver = new Thread(new ServerReceiver(fromRobot));

        System.out.println("Starting threads");
        sender.start();
        receiver.start();
        return true;
      }
    } catch (NXTCommException e) {
      e.printStackTrace();
    }

    // Connection did not open so return false;
    return false;
  }

  public static void main(String[] args) {
    BTServer us = new BTServer();
    us.open(new NXTInfo(btProtocol, "Dobot", "0016530FD7F4"));
  }
}
