package it.polimi.vovarini.common.network.server;

import it.polimi.vovarini.common.events.AbruptEndEvent;
import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GameEventListener;
import it.polimi.vovarini.common.events.GameEventManager;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.controller.Controller;
import it.polimi.vovarini.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{

  private Game game;
  private Controller controller;

  private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );

  public static final int DEFAULT_PORT = 6666;
  public static final int DEFAULT_MAX_THREADS = 4;

  private final ServerSocket serverSocket;

  private final ExecutorService pool;

  private int successfulDisconnects = 0;

  public static void handleUncaughtExceptions(Thread th, Throwable e) {
    LOGGER.log(Level.SEVERE, "Uncaught exception in thread " + th.toString() + ": " + e.getMessage());
    GameEventManager.raise(new AbruptEndEvent("server"));
    LOGGER.log(Level.SEVERE, "Raised AbruptEndEvent.");
  }

  public Server(int port, int numberOfPlayers) throws IOException{
    GameEventManager.bindListeners(this);
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
    init(numberOfPlayers);
  }

  public Server(int port, int numberOfPlayers, int nThreads) throws IOException{
    GameEventManager.bindListeners(this);
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(nThreads);
    init(numberOfPlayers);
  }

  private void init(int numberOfPlayers){
    try {
      game = new Game(numberOfPlayers);
      controller = new Controller(game);
      LOGGER.log(Level.INFO, "Server initialized.");
    } catch (InvalidNumberOfPlayersException e){
      e.printStackTrace();
    }
  }

  public void run(){
    LOGGER.log(Level.INFO, "Server is now listening on port {0}.", serverSocket.getLocalPort());
    try {
      while (!Thread.currentThread().isInterrupted()) {
        LOGGER.log(Level.FINE, "Waiting for new connection...");
        pool.execute(new RemoteView(serverSocket.accept()));
        LOGGER.log(Level.INFO, "A new client connected.");
      }
    } catch (IOException ex) {
      pool.shutdown();
    }
  }

  public void kill() {
    pool.shutdownNow();
  }

  public void shutdownAndAwaitTermination() {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  public Game getGame() {
    return game;
  }

  public Controller getController() {
    return controller;
  }

  @GameEventListener
  public void handleAbruptEnd(AbruptEndEvent e) {
    successfulDisconnects += 1;
    LOGGER.log(Level.SEVERE, "Server received AbruptEndEvent. Currently disconnected clients: " +
            successfulDisconnects);
    if (successfulDisconnects == (game.getInitialNumberOfPlayers() * 2)) { // one for the reader, one for the writer
      LOGGER.log(Level.INFO, "All clients disconnected, quitting...");
      System.exit(0);
    }
  }
}
