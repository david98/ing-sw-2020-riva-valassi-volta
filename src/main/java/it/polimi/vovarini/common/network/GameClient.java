package it.polimi.vovarini.common.network;

import it.polimi.vovarini.common.events.GameEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class is used by a client to communicate with the server
 * through the use of {@link GameEvent} objects.
 */
public class GameClient {

  private BlockingQueue<GameEvent> clientEvents;
  private BlockingQueue<GameEvent> serverEvents;

  private Socket socket;

  private final ExecutorService pool;

  /**
   * Creates a client which connects to the given IP address and port.
   * @param ip The server IPV4 address.
   * @param port The server port.
   * @throws IOException If an error occurs during the socket creation.
   */
  public GameClient(String ip, int port) throws IOException {
    socket = new Socket(ip, port);
    socket.setSoTimeout(1000);
    System.out.println("Connected to " + ip + ":" + port + ".");

    clientEvents = new LinkedBlockingQueue<>();
    serverEvents = new LinkedBlockingQueue<>();

    pool = Executors.newFixedThreadPool(2);
    pool.execute(new SocketWriter<>(socket, clientEvents));
    pool.execute(new SocketReader<>(socket, serverEvents, GameEvent.class));
  }

  /**
   * Sends an event to the server.
   * @param evt The event to send.
   */
  public void raise(GameEvent evt) {
    clientEvents.add(evt);
  }

  /**
   *
   * @return A queue containing events received from the server.
   */
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
