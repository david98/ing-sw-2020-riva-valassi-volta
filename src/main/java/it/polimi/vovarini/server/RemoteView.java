package it.polimi.vovarini.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.view.View;
import it.polimi.vovarini.view.ViewData;

public class RemoteView extends View implements ClientConnectionHandler {

  private final BlockingQueue<GameEvent> clientEvents;
  private final BlockingQueue<GameEvent> serverEvents;

  private final Socket clientSocket;

  private final ExecutorService pool;


  public RemoteView(Socket clientSocket) throws IOException {
    super();
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
        GameEventManager.raise(evt);
      } catch (InterruptedException e){
        e.printStackTrace();
        break;
      }
    }
  }

  @Override
  @GameEventListener
  public void handleBoardUpdate(BoardUpdateEvent e) {
    data.setBoard(e.getNewBoard());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
    data.setCurrentPlayer(e.getNewPlayer());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handlePhaseUpdate(PhaseUpdateEvent e) {
    data.setCurrentPhase(e.getNewPhase());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleGameStart(GameStartEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleNewPlayer(NewPlayerEvent e) {
    data.addPlayer(e.getNewPlayer().clone());
    serverEvents.add(e);
  }
}
