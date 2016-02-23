package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The receiver thread for the server. Receives messages from it's connected
 * NXT.
 * 
 * @author Reece
 *
 */
public class ServerReceiver implements Runnable {

  private boolean running;
  private DataInputStream fromRobot;

  /**
   * Create the receiver.
   * 
   * @param _fromRobot The DataInputStream from the NXT.
   */
  public ServerReceiver(DataInputStream fromRobot) {
    this.fromRobot = fromRobot;
  }

  @Override
  public void run() {
    try {

      System.out.println("Receiver running");

      running = true;
      while (running) {
        System.out.println("Waiting to read");
        String input = fromRobot.readUTF();
        System.out.println(input);
      }

    } catch (IOException e) {
      System.err.println("An NXT disconnected: " + e.getMessage());
    }
  }

}
