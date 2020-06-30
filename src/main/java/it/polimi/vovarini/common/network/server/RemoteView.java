package it.polimi.vovarini.common.network.server;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.SocketReader;
import it.polimi.vovarini.common.network.SocketWriter;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.view.View;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a client on the server.
 * Its role is to handle communication between the client and the server.
 *
 * @author Davide Volta
 */
public class RemoteView extends View implements Runnable {

  private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );

  private final BlockingQueue<GameEvent> clientEvents;
  private final BlockingQueue<GameEvent> serverEvents;

  private final Socket clientSocket;

  private final ExecutorService pool;

  /**
   * Constructs a RemoteView for the given client.
   * @param clientSocket An active socket connected with the client.
   * @throws IOException If I/O streams can't be created.
   */
  public RemoteView(Socket clientSocket) throws IOException {
    super();
    this.clientSocket = clientSocket;

    clientEvents = new LinkedBlockingQueue<>();
    serverEvents = new LinkedBlockingQueue<>();

    pool = Executors.newFixedThreadPool(2, runnable -> {
      Thread t = Executors.defaultThreadFactory().newThread(runnable);
      t.setUncaughtExceptionHandler(Server::handleUncaughtExceptions);
      return t;
    });
    pool.execute(new SocketWriter<>(clientSocket, serverEvents));
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
  public void handle(BoardUpdateEvent e) {
    data.setBoard(e.getNewBoard());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(CurrentPlayerChangedEvent e) {
    data.setCurrentPlayer(e.getNewPlayer());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(PhaseUpdateEvent e) {
    data.setCurrentPhase(e.getNewPhase());
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(GameStartEvent e) {
    for (Player p: e.getPlayers()){
      data.addPlayer(p);
    }
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(NewPlayerEvent e) {
    super.handle(e);
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(GodSelectionStartEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(SelectYourCardEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(CardAssignmentEvent e) {
    serverEvents.add(e);
  }


  @Override
  @GameEventListener
  public void handle(PlaceYourWorkersEvent e) {
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(PlayerInfoUpdateEvent e) {
    super.handle(e);
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(GodCardUpdateEvent e) {
    super.handle(e);
    serverEvents.add(e);
  }

  /**
   * Handles the RegistrationEvent which has been raised by the client.
   * 
   * @param e A RegistrationEvent.
   */
  public void handleRegistrationEvent(RegistrationEvent e) {
    data.setOwner(new Player(e.getNickname()));
  }

  @Override
  @GameEventListener
  public void handle(VictoryEvent e) { serverEvents.add(e); }

  @Override
  @GameEventListener
  public void handle(LossEvent e) { serverEvents.add(e); }

  @Override
  @GameEventListener
  public void handle(AbruptEndEvent e) {
    LOGGER.log(Level.INFO, "AbruptEndEvent forwarded to client.");
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(FirstPlayerEvent e) {
    LOGGER.log(Level.INFO, "FirstPlayerEvent forwarded to client.");
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(RegistrationStartEvent e) {
    LOGGER.log(Level.INFO, "RegistrationStartEvent forwarded to client.");
    serverEvents.add(e);
  }

  @Override
  @GameEventListener
  public void handle(InvalidNicknameEvent e) {
    serverEvents.add(e);
  }
}
