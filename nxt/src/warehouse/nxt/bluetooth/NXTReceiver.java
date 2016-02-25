package warehouse.nxt.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Queue;

/**
 * The receiver thread for the NXT. Receives messages from the server the NXT is
 * connected to.
 * 
 * @author Reece
 *
 */
public class NXTReceiver implements Runnable {

  private DataInputStream fromServer;
  private Queue<String> fromServerQueue;
  private boolean running;

  /**
   * Create the NXTReceiver.
   * 
   * @param fromServer The DataInputStream from the server.
   */
  public NXTReceiver(DataInputStream fromServer) {
    this.fromServer = fromServer;
    fromServerQueue = new Queue<>();
  }

  @Override
  public void run() {
    try {

      System.out.println("Receiver running");

      running = true;
      while (running) {
        String input = fromServer.readUTF();
        System.out.println(input);
        fromServerQueue.addElement(input);
      }

    } catch (IOException e) {
      System.err.println("Reading failed");
    }
  }
}
