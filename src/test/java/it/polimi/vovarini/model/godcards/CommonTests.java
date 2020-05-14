package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.common.network.server.Server;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonTests {

  private static final File f = new File(System.getProperty("java.io.tmpdir"), "x.ser");
  private final ExecutorService pool = Executors.newFixedThreadPool(2);


  @ParameterizedTest
  @EnumSource(GodName.class)
  @DisplayName("Tests that a GodCard can be serialized and deserialized")
  void serializationAndDeserialization(GodName name) throws IOException, ClassNotFoundException {
    GodCard godCard = GodCardFactory.create(name);

    try(FileOutputStream os=new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(os)) {
      oos.writeObject(godCard);
    }
    System.out.println("Written to " + f);

    try(FileInputStream is=new FileInputStream(f);
        ObjectInputStream ois=new ObjectInputStream(is)) {
      GodCard cardRead = (GodCard) ois.readObject();
      assertEquals(godCard, cardRead);
    }
    System.out.println("Read from " + f);
  }


  @Test
  @Disabled
  @DisplayName("Tests that a GodCard clone can be serialized and deserialized over a socket")
  void serializationAndDeserializationSocket() throws IOException, ClassNotFoundException {
    pool.execute(() -> {
      try{
        GodCard nb = GodCardFactory.create(GodName.Nobody);
        ServerSocket serverSocket = new ServerSocket(Server.DEFAULT_PORT);
        Socket clientSocket = serverSocket.accept();
        try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
          oos.writeObject(nb);
        }
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    int count = 0;
    int maxTries = 10;
    while(true) {
      try {
        Socket socket = new Socket("127.0.0.1", Server.DEFAULT_PORT);

        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
          Object obj = ois.readObject();
          GodCard nb = (GodCard) obj;
        }
        return;
      } catch (ConnectException e) {
        // handle exception
        if (++count == maxTries) throw e;
      }
    }
  }

  @ParameterizedTest
  @EnumSource(GodName.class)
  @DisplayName("Test that equals works")
  void testEquals(GodName name){
    GodCard card = GodCardFactory.create(name);
    assertEquals(card, card);
  }

  @ParameterizedTest
  @EnumSource(GodName.class)
  @DisplayName("Test that hashCode works")
  void testHashCode(GodName name){
    GodCard original = GodCardFactory.create(name);
    GodCard clone = GodCardFactory.clone(original);

    assertNotSame(original, clone);
    assertEquals(original, clone);
    assertEquals(original.hashCode(), clone.hashCode());
  }

  @ParameterizedTest
  @EnumSource(GodName.class)
  @DisplayName("Test that clone works and produces two GodCard that are equal but not the same object")
  void testClone(GodName name){
    GodCard original = GodCardFactory.create(name);
    GodCard clone = GodCardFactory.clone(original);

    assertNotSame(original, clone);
    assertEquals(original, clone);
  }

}
