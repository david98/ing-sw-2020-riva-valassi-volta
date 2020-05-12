package it.polimi.vovarini.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketReader<T> implements Runnable{
  private final Socket socket;

  private final ObjectInputStream in;

  private final BlockingQueue<T> retrievedObjects;

  private final Class<T> objClass;

  private boolean running;

  public SocketReader(Socket socket, BlockingQueue<T> retrievedObjects, Class<T> objClass) throws IOException {
    this.socket = socket;
    in = new ObjectInputStream(socket.getInputStream());
    this.retrievedObjects = retrievedObjects;
    this.objClass = objClass;

    running = false;
  }

  public void run(){
    running = true;
    while (running){
      try{
        Object receivedObj = in.readObject();
        if (objClass.isAssignableFrom(receivedObj.getClass())){
          retrievedObjects.add(objClass.cast(receivedObj));
        }
      } catch (IOException | ClassNotFoundException e){
        e.printStackTrace();
        break;
      }
    }
  }

  public void stop(){
    running = false;
  }

}
