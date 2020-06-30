package it.polimi.vovarini.common.network.server;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.controller.Controller;
import it.polimi.vovarini.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the game server, which is meant to be run as a thread.
 *
 * @author Davide Volta
 */
public class Server implements Runnable{

  private Game game;
  private Controller controller;

  private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );

  public static final int DEFAULT_PORT = 6666;
  public static final int DEFAULT_MAX_THREADS = 4;

  private final ServerSocket serverSocket;

  private final ExecutorService pool;

  private int successfulDisconnects = 0;

  private RemoteView[] remoteViews = new RemoteView[Game.MAX_PLAYERS];
  private int currentlyConnectedClients = 0;

  /**
   * This method handles an uncaught exception inside a thread and raises
   * a {@link AbruptEndEvent}.
   * @param th The thread in which the exception first originated.
   * @param e The exception as a {@link Throwable}.
   */
  public static void handleUncaughtExceptions(Thread th, Throwable e) {
    LOGGER.log(Level.SEVERE, "Uncaught exception in thread " + th.toString() + ": " + e.getMessage());
    GameEventManager.raise(new AbruptEndEvent("server"));
    LOGGER.log(Level.SEVERE, "Raised AbruptEndEvent.");
  }

  /**
   * Constructs a server listening on the specified port.
   * @param port The port to listen on.
   * @throws IOException If a ServerSocket can't be allocated.
   */
  public Server(int port) throws IOException{
    GameEventManager.bindListeners(this);
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
  }

  /**
   * Constructs a server listening on the specified port,
   * which will run at most nThreads {@link RemoteView}.
   * @param port The port to listen on.
   * @param nThreads The maximum number of concurrent {@link RemoteView}.
   * @throws IOException If a ServerSocket can't be allocated.
   */
  public Server(int port, int nThreads) throws IOException{
    GameEventManager.bindListeners(this);
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(nThreads);
  }

  private void init(int numberOfPlayers){
    try {
      game = new Game(numberOfPlayers);
      controller = new Controller(game);
      LOGGER.log(Level.INFO, "Game initialized.");
    } catch (InvalidNumberOfPlayersException e){
      e.printStackTrace();
    }
  }

  public void run(){
    LOGGER.log(Level.INFO, "Server is now listening on port {0}.", serverSocket.getLocalPort());
    while (!Thread.currentThread().isInterrupted()) {
      LOGGER.log(Level.FINE, "Waiting for new connection...");
      acceptNewClient();
      if (game != null && currentlyConnectedClients >= game.getInitialNumberOfPlayers()) {
        GameEventManager.raise(new RegistrationStartEvent("server"));
      }
    }
  }

  private void acceptNewClient() {
    try {
      Socket clientSocket = serverSocket.accept();

      if (game == null && currentlyConnectedClients <= 0) {
        remoteViews[currentlyConnectedClients] = new RemoteView(clientSocket);
        pool.execute(remoteViews[currentlyConnectedClients]);
        LOGGER.log(Level.INFO, "First client connected.");
        GameEventManager.raise(new FirstPlayerEvent("server"));
        LOGGER.log(Level.INFO, "FirstPlayerEvent raised.");
        currentlyConnectedClients++;
      } else if (currentlyConnectedClients > 0 && game != null && currentlyConnectedClients < game.getInitialNumberOfPlayers()){
        remoteViews[currentlyConnectedClients] = new RemoteView(clientSocket);
        pool.execute(remoteViews[currentlyConnectedClients]);
        LOGGER.log(Level.INFO, "A new client connected.");
        currentlyConnectedClients++;
      } else {
        clientSocket.close();
        LOGGER.log(Level.INFO, "Connection refused.");
      }
    } catch (IOException ignored) {

    }
  }

  /**
   * Instantly kills all running remote views.
   */
  public void kill() {
    pool.shutdownNow();
  }

  public Game getGame() {
    return game;
  }

  public Controller getController() {
    return controller;
  }

  /**
   * Handles an abrupt end.
   * @param e An AbruptEndEvent.
   */
  @GameEventListener
  public void handle(AbruptEndEvent e) {
    successfulDisconnects += 1;
    LOGGER.log(Level.SEVERE, "Server received AbruptEndEvent. Currently disconnected clients: " +
            successfulDisconnects);
    if (successfulDisconnects == (game.getInitialNumberOfPlayers() * 2)) { // one for the reader, one for the writer
      LOGGER.log(Level.INFO, "All clients disconnected, quitting...");
      System.exit(0);
    }
  }

  /**
   * Handles the event raised by the first player
   * when they have chosen the number of players.
   * @param e
   */
  @GameEventListener
  public void handle(NumberOfPlayersChoiceEvent e){
    if (e.getNumberOfPlayers() < Game.MIN_PLAYERS || e.getNumberOfPlayers() > Game.MAX_PLAYERS) {
      throw new InvalidNumberOfPlayersException();
    }
    init(e.getNumberOfPlayers());
  }
}
