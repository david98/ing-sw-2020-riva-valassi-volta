package it.polimi.vovarini.common.network;

import it.polimi.vovarini.common.events.GameEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class GameClient {

  private BlockingQueue<GameEvent> clientEvents;
  private BlockingQueue<GameEvent> serverEvents;

  private Socket socket;

  private final ExecutorService pool;

  public GameClient(String ip, int port) throws IOException {
    socket = new Socket(ip, port);
    socket.setSoTimeout(1000);
    System.out.println("Connected to " + ip + ":" + port + ".");

    clientEvents = new LinkedBlockingQueue<>();
    serverEvents = new LinkedBlockingQueue<>();

    pool = Executors.newFixedThreadPool(2);
    pool.execute(new SocketWriter<>(socket, clientEvents, GameEvent.class));
    pool.execute(new SocketReader<>(socket, serverEvents, GameEvent.class));
  }

  public void raise(GameEvent evt) {
    clientEvents.add(evt);
  }

  public BlockingQueue<GameEvent> getServerEvents() {
    return serverEvents;
  }

  public String getIPv4Address(){
    return socket.getLocalAddress().getHostAddress();
  }

  public void setSocketTimeout(int milliseconds) {
    try {
      socket.setSoTimeout(milliseconds);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public int getSocketTimeout() {
    try {
      return socket.getSoTimeout();
    } catch (SocketException e) {
      return -1;
    }
  }
}
