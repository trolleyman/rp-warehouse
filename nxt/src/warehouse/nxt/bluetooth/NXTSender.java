package warehouse.nxt.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.util.Delay;

public class NXTSender implements Runnable {

  private DataOutputStream toServer;
  private boolean running;
  
  public NXTSender(DataOutputStream _toServer) {
    this.toServer = _toServer;
  }
  
  @Override
  public void run() {
    System.out.println("Sender running");
    running = true;
    
    try {
      
      while (running) {
        System.out.println("Send hi");
        toServer.writeUTF("Hello world!");
        toServer.flush();
        Delay.msDelay(1000);
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
