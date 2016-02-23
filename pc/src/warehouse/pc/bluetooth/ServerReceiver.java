package warehouse.pc.bluetooth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerReceiver implements Runnable {

  private boolean running;
  private DataInputStream fromRobot;

  public ServerReceiver(DataInputStream _fromRobot) {
    this.fromRobot = _fromRobot;
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
