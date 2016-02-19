package warehouse.pc.bluetooth;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

      running = true;
      while (running) {

        // Open the connection to the NXT and open data streams
        System.out.println("Tring to open a connection");
        if (comm.open(nxts[0])) {
          ObjectOutputStream toRobot = new ObjectOutputStream(comm.getOutputStream());
          ObjectInputStream fromRobot = new ObjectInputStream(comm.getInputStream());

          Thread sender = new Thread(new ServerSender());
          Thread receiver = new Thread(new ServerReceiver(fromRobot));

          sender.start();
          receiver.start();
        }
      }

    } catch (NXTCommException e) {
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("A NXT has disconnected!");
    }
    
    System.out.println("Ended");
  }
}
