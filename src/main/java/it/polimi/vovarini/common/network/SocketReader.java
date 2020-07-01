package it.polimi.vovarini.common.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

/**
 * This class fills a queue asynchronously with objects of class T retrieved from a socket.
 *
 * @param <T> The class of the objects to be read from the socket.
 * @author Davide Volta
 */
public class SocketReader<T> implements Runnable {
  private final Socket socket;

  private final ObjectInputStream in;

  private final BlockingQueue<T> retrievedObjects;

  private final Class<T> objClass;

  /**
   * Constructs a reader reading objects from the given socket.
   *
   * @param socket           The active socket to read from.
   * @param retrievedObjects A queue where the objects will be written to.
   * @param objClass         Needed to programmatically cast objects through reflection.
   * @throws IOException If an {@link ObjectInputStream} can't be created.
   */
  public SocketReader(Socket socket, BlockingQueue<T> retrievedObjects, Class<T> objClass) throws IOException {
    this.socket = socket;
    in = new ObjectInputStream(socket.getInputStream());
    this.retrievedObjects = retrievedObjects;
    this.objClass = objClass;
  }

  /**
   * Runs the instance of this SocketReader
   */
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Object receivedObj = in.readObject();
        if (objClass.isAssignableFrom(receivedObj.getClass())) {
          retrievedObjects.put(objClass.cast(receivedObj));
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (SocketTimeoutException ignored) {
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
