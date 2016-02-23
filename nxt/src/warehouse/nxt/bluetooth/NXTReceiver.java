package warehouse.nxt.bluetooth;

import java.io.DataInputStream;

public class NXTReceiver implements Runnable {

  private DataInputStream fromServer;

  public NXTReceiver(DataInputStream fromServer) {
    this.fromServer = fromServer;
  }
  
  @Override
  public void run() {
    System.out.println("Receiver running");
  }
}
