package warehouse.pc.bluetooth;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerReceiver implements Runnable {

  private boolean running;
  private ObjectInputStream fromRobot;

  public ServerReceiver(ObjectInputStream _fromRobot) {
    this.fromRobot = _fromRobot;
  }

  @Override
  public void run() {
    try {
      
      System.out.println("Receiver running");
      
      running = true;
      while (running) {
        String input = fromRobot.readUTF();
        System.out.println(input);
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
