package warehouse.pc.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

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
  private LinkedBlockingQueue<String> fromRobotQueue;

  /**
   * Create the receiver.
   * 
   * @param fromRobot The DataInputStream from the NXT.
   * @param fromRobotQueue The queue to put received messages into.
   */
  public ServerReceiver(DataInputStream fromRobot, LinkedBlockingQueue<String> fromRobotQueue) {
    this.fromRobot = fromRobot;
    this.fromRobotQueue = fromRobotQueue;
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
        fromRobotQueue.put(input);
      }

    } catch (IOException e) {
      System.err.println("An NXT disconnected: " + e.getMessage());
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.err.println("Receiver was interupted for some reason");
    }
  }

}
