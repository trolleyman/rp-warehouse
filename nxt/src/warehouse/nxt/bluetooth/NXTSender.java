package warehouse.nxt.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The sender thread for the NXT. Sends threads to the server that the NXT is
 * connected to.
 * 
 * @author Reece
 *
 */
public class NXTSender {

  private DataOutputStream toServer;

  /**
   * Create the NXTSender.
   * 
   * @param _toServer The DataOutputStream to the server.
   */
  public NXTSender(DataOutputStream toServer) {
    this.toServer = toServer;
  }
  
  public void sendToServer(String message) {
  	try {
  		
  		toServer.writeUTF(message);
  		toServer.flush();
  		
  	} catch (IOException e) {
  		e.printStackTrace();
  	}
  }
}
