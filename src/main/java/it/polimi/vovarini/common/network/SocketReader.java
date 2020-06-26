package it.polimi.vovarini.common.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class SocketReader<T> implements Runnable{
  private final Socket socket;

  private final ObjectInputStream in;

  private final BlockingQueue<T> retrievedObjects;

  private final Class<T> objClass;

  public SocketReader(Socket socket, BlockingQueue<T> retrievedObjects, Class<T> objClass) throws IOException {
    this.socket = socket;
    in = new ObjectInputStream(socket.getInputStream());
    this.retrievedObjects = retrievedObjects;
    this.objClass = objClass;
  }

  public void run(){
    while (!Thread.currentThread().isInterrupted()){
      try{
        Object receivedObj = in.readObject();
        if (objClass.isAssignableFrom(receivedObj.getClass())){
          retrievedObjects.put(objClass.cast(receivedObj));
        }
      } catch (ClassNotFoundException e){
        e.printStackTrace();
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
      } catch (IOException e){
        throw new RuntimeException(e);
      }
    }
  }
}
