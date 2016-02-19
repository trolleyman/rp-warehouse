package warehouse.nxt.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

public class NXTSender extends Thread {

  private DataOutputStream toServer;
  private boolean running;
  
  public NXTSender(DataOutputStream _toServer) {
    this.toServer = _toServer;
  }
  
  @Override
  public void run() {
    try {
      
      toServer.writeChars("Hello world!");
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
