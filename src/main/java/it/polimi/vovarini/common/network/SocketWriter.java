package it.polimi.vovarini.common.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

/**
 * This class asynchronously writes objects retrieved from a queue to a socket.
 * @param <T> The class of the objects to be written to the socket.
 *
 * @author Davide Volta
 */
public class SocketWriter<T> implements Runnable{
  private final Socket socket;

  private final ObjectOutputStream out;

  private final BlockingQueue<T> objectsToBeWritten;

  /**
   * Constructs a writer reading from objectsToBeWritten and writing to the socket.
   * @param socket The active socket to write objects to.
   * @param objectsToBeWritten A queue from which objects will be read.
   * @throws IOException If an {@link ObjectOutputStream} can't be created.
   */
  public SocketWriter(Socket socket, BlockingQueue<T> objectsToBeWritten) throws IOException {
    this.socket = socket;
    out = new ObjectOutputStream(socket.getOutputStream());
    this.objectsToBeWritten = objectsToBeWritten;
  }

  public void run(){
    while (!Thread.currentThread().isInterrupted()){
      try {
        T obj = objectsToBeWritten.take();
        out.writeObject(obj);
        out.flush();
      } catch (SocketTimeoutException ignored){
      } catch (IOException e){
        throw new RuntimeException(e);
      } catch (InterruptedException ignored){
        Thread.currentThread().interrupt();
      }
    }
  }
}
