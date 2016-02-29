package warehouse.nxt.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The receiver thread for the NXT. Receives messages from the server the NXT is
 * connected to.
 * 
 * @author Reece
 *
 */
public class NXTReceiver implements Runnable {

  private DataInputStream fromServer;
  private boolean running;
	private NXTClient client;

  /**
   * Create the NXTReceiver.
   * 
   * @param fromServer The DataInputStream from the server.
   */
  public NXTReceiver(DataInputStream fromServer, NXTClient client) {
    this.fromServer = fromServer;
    this.client = client;
  }

  @Override
  public void run() {
    try {

      System.out.println("Receiver running");

      running = true;
      while (running) {
        String input = fromServer.readUTF();
        System.out.println(input);
        doCommand(input);
      }

    } catch (IOException e) {
      System.err.println("Reading failed");
    }
  }
  
  private void doCommand(String command) {
  	switch(command) {
  		case "forward":
  			System.out.println("Go forward");
  			client.sendToServer("ready");
  			break;
  			
  		case "backward":
  			System.out.println("Go backward");
  			client.sendToServer("ready");
  			break;
  			
  		case "left":
  			System.out.println("Go left");
  			client.sendToServer("ready");
  			break;
  			
  		case "right":
  			System.out.println("Go right");
  			client.sendToServer("ready");
  			break;
  	}
  }
}
