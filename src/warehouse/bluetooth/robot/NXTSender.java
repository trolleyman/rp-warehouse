package warehouse.bluetooth.robot;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

import warehouse.bluetooth.Command;
import warehouse.shared.Robot;

public class NXTSender extends Thread {

  private ObjectOutputStream toServer;
  private BlockingQueue<Command> commands;
  private Robot robot;
  private boolean running;
  
  public NXTSender(Robot _robot, ObjectOutputStream _toServer, BlockingQueue<Command> _commands) {
    this.robot = _robot;
    this.toServer = _toServer;
    this.commands = _commands;
  }
  
  @Override
  public void run() {
    try {
      
      // First pass the robot object to the server
      toServer.writeObject(robot);
      
      running = true;
      while (running) {
        // Wait until we get a command
        Command command = commands.take();
        
        // Send it to the server
        toServer.writeObject(command);
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.err.println("command.Take() was interupted somehow, retrying...");
      e.printStackTrace();
    }
  }
}
