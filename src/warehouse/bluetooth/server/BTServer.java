package warehouse.bluetooth.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class BTServer {
  
  public BTServer() {
    
  }

  public static void main(String[] args) {
    try {
      int btProtocol = NXTCommFactory.BLUETOOTH;
      
      // Create the comms system for this OS and driver
      NXTComm comm = NXTCommFactory.createNXTComm(btProtocol);
      
      // Add NXT names and IDs here
      NXTInfo[] nxts = {new NXTInfo(btProtocol, "Dobot", "0016530FD7F4")};
      
      // Open the connection to the NXT and open object streams
      comm.open(nxts[0]);
      ObjectOutputStream toRobot = new ObjectOutputStream(comm.getOutputStream());
      ObjectInputStream fromRobot = new ObjectInputStream(comm.getInputStream());
      
      Thread sender = new Thread(new ServerSender());
      Thread receiver = new Thread(new ServerReceiver());
      
      sender.start();
      receiver.start();
      
    } catch (NXTCommException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
