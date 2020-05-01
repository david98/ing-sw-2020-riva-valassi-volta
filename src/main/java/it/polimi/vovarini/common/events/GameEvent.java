package it.polimi.vovarini.common.events;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;

public abstract class GameEvent extends EventObject implements Serializable {

  public GameEvent(Object source){
    super(source);
  }

  private void writeObject(final ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();

    Serializable serSource = (Serializable) source;
    out.writeObject(serSource);
  }

  private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    source = in.readObject();
  }

}
