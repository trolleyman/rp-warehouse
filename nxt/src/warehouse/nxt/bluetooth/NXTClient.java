package warehouse.nxt.bluetooth;

import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class NXTClient {

  public static void main(String[] args) {

    System.out.println("NXTClient");
    System.out.println("Waiting for BT");
    BTConnection connection = Bluetooth.waitForConnection();
    System.out.println("Got a connection!");
    
    System.out.println("Creating streams");
    
    Button.waitForAnyPress();
    connection.close();
    System.exit(0);
  }

}
