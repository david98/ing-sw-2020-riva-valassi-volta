package it.polimi.vovarini.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketWriter<T> implements Runnable{
  private final Socket socket;

  private final ObjectOutputStream out;

  private final BlockingQueue<T> objectsToBeWritten;

  private final Class<T> objClass;

  private boolean running;

  public SocketWriter(Socket socket, BlockingQueue<T> objectsToBeWritten, Class<T> objClass) throws IOException {
    this.socket = socket;
    out = new ObjectOutputStream(socket.getOutputStream());
    this.objectsToBeWritten = objectsToBeWritten;
    this.objClass = objClass;

    running = false;
  }

  public void run(){
    running = true;
    while(running){
      try {
        T obj = objectsToBeWritten.take();
        out.writeObject(obj);
        out.flush();
      } catch (IOException e){
        e.printStackTrace();
        break;
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
