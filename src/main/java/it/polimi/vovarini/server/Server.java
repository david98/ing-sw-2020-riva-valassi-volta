package it.polimi.vovarini.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable{

  public static final int DEFAULT_PORT = 6666;
  public static final int DEFAULT_MAX_THREADS = 4;

  private ServerSocket serverSocket;

  private final ExecutorService pool;

  public Server(int port) throws IOException{
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
  }

  public Server(int port, int nThreads) throws IOException{
    serverSocket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(nThreads);
  }

  public void run(){
    try {
      while (true) {
        pool.execute(new RemoteView(serverSocket.accept()));
        System.out.println("nuova connessione!");
      }
    } catch (IOException ex) {
      pool.shutdown();
    }
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
    server.run();
  }
}
