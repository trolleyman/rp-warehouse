package warehouse.pc.bluetooth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class BTServer {

  private static boolean running;

  public BTServer() {

  }

  public static void main(String[] args) {
    try {

      // Create the comms system for this OS and driver
      int btProtocol = NXTCommFactory.BLUETOOTH;
      NXTComm comm = NXTCommFactory.createNXTComm(btProtocol);

      // Add NXT names and IDs here
      NXTInfo[] nxts = { new NXTInfo(btProtocol, "Dobot", "0016530FD7F4") };

        // Open the connection to the NXT and open data streams
        System.out.println("Tring to open a connection");
        if (comm.open(nxts[0])) {
          System.out.println("Making stream and reader");
          DataOutputStream toRobot = new DataOutputStream(comm.getOutputStream());
          DataInputStream fromRobot = new DataInputStream(comm.getInputStream());

          System.out.println("Creating threads");
          Thread sender = new Thread(new ServerSender());
          Thread receiver = new Thread(new ServerReceiver(fromRobot));

          System.out.println("Starting threads");
          sender.start();
          receiver.start();
        }

    } catch (NXTCommException e) {
      e.printStackTrace();
      System.err.println("An NXT has disconnected");
    }
    
    System.out.println("Ended");
  }
}
