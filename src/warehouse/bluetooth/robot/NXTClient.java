package warehouse.bluetooth.robot;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class NXTClient {

  public static void main(String[] args) {

    System.out.println("NXTClient");
    System.out.println("Waiting for BT");
    BTConnection connection = Bluetooth.waitForConnection();
    System.out.println("Connected!");
    
  }

}
