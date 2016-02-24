package warehouse.pc.bluetooth;

import lejos.pc.comm.NXTInfo;

public class TestOpenServer {

  public static void main(String[] args) {
    BTServer server = new BTServer();
    server.open(new NXTInfo(BTServer.btProtocol, "Dobot", "0016530FD7F4"));
  }
}
