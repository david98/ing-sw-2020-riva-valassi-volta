package it.polimi.vovarini.server;

import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.WorkerSelectionEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.view.GameView;

import java.io.*;
import java.net.*;
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
    System.out.println("Connected to " + ip + ":" + port + ".");

    clientEvents = new LinkedBlockingQueue<>();
    serverEvents = new LinkedBlockingQueue<>();

    pool = Executors.newFixedThreadPool(2);
    pool.execute(new SocketWriter<>(socket, clientEvents, GameEvent.class));
    pool.execute(new SocketReader<>(socket, serverEvents, GameEvent.class));
  }

  public void send(GameEvent evt) {
    clientEvents.add(evt);
  }

  public static void main(String[] args) throws IOException {
    GameClient client = new GameClient("127.0.0.1", 6666);
    WorkerSelectionEvent evt = new WorkerSelectionEvent(client, Sex.Male);
    client.send(evt);
  }
}
