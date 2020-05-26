package it.polimi.vovarini.common.network.server;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.SocketReader;
import it.polimi.vovarini.common.network.SocketWriter;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.View;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
    while (!Thread.currentThread().isInterrupted()){
      try {
        GameEvent evt = clientEvents.take();
        if (evt instanceof RegistrationEvent) { // oh yeah instanceof
          handleRegistrationEvent((RegistrationEvent) evt);
        }
        GameEventManager.raise(evt);
      } catch (InterruptedException ignored){
        Thread.currentThread().interrupt();
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
    for (Player p: e.getPlayers()){
      data.addPlayer(p);
    }
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleNewPlayer(NewPlayerEvent e) {
    super.handleNewPlayer(e);
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleGodSelectionStart(GodSelectionStartEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleSelectYourCard(SelectYourCardEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleCardAssignment(CardAssignmentEvent e) {
    serverEvents.add(e);
  }


  @Override
  @GameEventListener
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e) {
    super.handlePlayerInfoUpdate(e);
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handleGodCardUpdate(GodCardUpdateEvent e) {
    super.handleGodCardUpdate(e);
    serverEvents.add(e);
  }

  public void handleRegistrationEvent(RegistrationEvent e) {
    data.setOwner(new Player(e.getNickname()));
  }
}
