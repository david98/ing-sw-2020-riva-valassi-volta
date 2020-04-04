package it.polimi.vovarini.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

  private static final int PORT = 12345;
  private ServerSocket serverSocket;
  private ExecutorService executor = Executors.newFixedThreadPool(128);

  public Server() throws IOException {
    this.serverSocket = new ServerSocket(PORT);
  }

  public synchronized void lobby(ClientConnection c, String name) {};
}
