package it.polimi.vovarini.model.godcards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

public class CommonTests {

  static final File f = new File(System.getProperty("java.io.tmpdir"), "x.ser");

  @Test
  @DisplayName("Tests that a GodCard can be serialized and deserialized")
  void serializationAndDeserialization() throws IOException, ClassNotFoundException {
    GodCard nb = GodCardFactory.create(GodName.Nobody);

    try(FileOutputStream os=new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(os)) {
      oos.writeObject(nb);
    }
    System.out.println("Written to " + f);

    try(FileInputStream is=new FileInputStream(f);
        ObjectInputStream ois=new ObjectInputStream(is)) {
      GodCard nbRead = (GodCard) ois.readObject();
    }
    System.out.println("Read from " + f);
  }
}
