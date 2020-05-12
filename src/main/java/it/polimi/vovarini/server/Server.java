package it.polimi.vovarini.server;

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

  private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );

  public static final int DEFAULT_PORT = 6666;
  public static final int DEFAULT_MAX_THREADS = 4;

  private final ServerSocket serverSocket;

  private final ExecutorService pool;

  private boolean running;

  public Server(int port) throws IOException{
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
    init();
  }

  public Server(int port, int nThreads) throws IOException{
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(nThreads);
    init();
  }

  private void init(){
    try {
      Game game = new Game(2);
      Controller controller = new Controller(game);
      LOGGER.log(Level.INFO, "Server initialized.");
      running = false;
    } catch (InvalidNumberOfPlayersException e){
      e.printStackTrace();
    }
  }

  public void run(){
    running = true;
    LOGGER.log(Level.INFO, "Server is now listening on port {0}.", serverSocket.getLocalPort());
    try {
      while (running) {
        LOGGER.log(Level.FINE, "Waiting for new connection...");
        pool.execute(new RemoteView(serverSocket.accept()));
        LOGGER.log(Level.INFO, "A new client connected.");
      }
    } catch (IOException ex) {
      pool.shutdown();
    }
  }

  public void stop(){
    running = false;
  }

  public void shutdownAndAwaitTermination(ExecutorService pool) {
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

  public static void main(String[] args) throws IOException {
    Server server = new Server(DEFAULT_PORT);
    Thread thread = new Thread(server);
    thread.start();
  }
}
