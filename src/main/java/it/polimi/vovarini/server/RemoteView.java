package it.polimi.vovarini.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import it.polimi.vovarini.common.events.*;

public class RemoteView implements ClientConnectionHandler {

  private BlockingQueue<GameEvent> clientEvents;
  private BlockingQueue<GameEvent> serverEvents;

  private final Socket clientSocket;

  private final ExecutorService pool;

  public RemoteView(Socket clientSocket) throws IOException {
    this.clientSocket = clientSocket;

    clientEvents = new LinkedBlockingQueue<>();
    serverEvents = new LinkedBlockingQueue<>();

    pool = Executors.newFixedThreadPool(2);

    pool.execute(new SocketWriter<>(clientSocket, serverEvents, GameEvent.class));
    pool.execute(new SocketReader<>(clientSocket, clientEvents, GameEvent.class));
  }

  @Override
  public void run() {
    while (true){
      try {
        GameEvent evt = clientEvents.take();
        System.out.println(evt.toString());
      } catch (InterruptedException e){
        e.printStackTrace();
        break;
      }
    }
  }
}
