package warehouse.pc.bluetooth;

import java.io.DataOutputStream;

/**
 * The sender thread for the server. Sends messages to it's connected NXT.
 * 
 * @author Reece
 *
 */
public class ServerSender implements Runnable {

  private DataOutputStream toRobot;

  /**
   * Create the ServerSender.
   * 
   * @param toRobot The DataOutputStream to the NXT.
   */
  public ServerSender(DataOutputStream toRobot) {
    this.toRobot = toRobot;
  }

  @Override
  public void run() {
    System.out.println("Sender running");
  }

}
