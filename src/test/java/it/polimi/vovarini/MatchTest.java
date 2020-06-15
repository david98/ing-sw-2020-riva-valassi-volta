package it.polimi.vovarini;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class MatchTest {

  private static final GodName[] availableGodCards = {GodName.Zeus, GodName.Apollo, GodName.Minotaur};
  private static final GodName davideCard = GodName.Minotaur;
  private static final GodName marcoCard = GodName.Apollo;
  private static final GodName mattiaCard = GodName.Zeus;

  private Thread serverThread;
  private Server server;
  private GameClient davideClient, marcoClient, mattiaClient;
  private Player davide, marco, mattia;

  void godSelection() throws IOException, InterruptedException {
    server = new Server(Server.DEFAULT_PORT, 3);
    serverThread = new Thread(server);
    serverThread.start();

    davideClient = new GameClient("localhost", Server.DEFAULT_PORT);
    davideClient.raise(new RegistrationEvent("localhost", "davide"));
    davideClient.getServerEvents().take(); //wait for new player event, needed for synchronization
    davide = new Player("davide");
    marcoClient = new GameClient("localhost", Server.DEFAULT_PORT);
    marcoClient.raise(new RegistrationEvent("localhost", "marco"));
    davideClient.getServerEvents().take(); //wait for new player event, needed for synchronization
    marcoClient.getServerEvents().take(); //wait for new player event, needed for synchronization
    marco = new Player("marco");
    mattiaClient = new GameClient("localhost", Server.DEFAULT_PORT);
    mattiaClient.raise(new RegistrationEvent("localhost", "mattia"));
    davideClient.getServerEvents().take(); //wait for new player event, needed for synchronization
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take(); //wait for new player event, needed for synchronization
    mattia = new Player("mattia");

    // wait for GodSelectionStart
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) { // whoever is elected
      case "davide" -> davideClient.raise(new AvailableCardsEvent(davide, availableGodCards));
      case "marco" -> marcoClient.raise(new AvailableCardsEvent(marco, availableGodCards));
      case "mattia" -> mattiaClient.raise(new AvailableCardsEvent(mattia, availableGodCards));
    }

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for SelectYourCardEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) {
      case "davide" -> davideClient.raise(new CardChoiceEvent(davide, davideCard));
      case "marco" -> marcoClient.raise(new CardChoiceEvent(marco, marcoCard));
      case "mattia" -> mattiaClient.raise(new CardChoiceEvent(mattia, mattiaCard));
    }

    // wait for CardAssignmentEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for SelectYourCardEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) {
      case "davide" -> davideClient.raise(new CardChoiceEvent(davide, davideCard));
      case "marco" -> marcoClient.raise(new CardChoiceEvent(marco, marcoCard));
      case "mattia" -> mattiaClient.raise(new CardChoiceEvent(mattia, mattiaCard));
    }

    // wait for CardAssignmentEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CardAssignmentEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();
  }

  void placeWorkers() throws InterruptedException {
    // wait for PlaceYourWorkersEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) {
      case "davide" -> {
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Male));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 1))); //this is the one that will push
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Female));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 2)));
      }
      case "marco" -> {
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Male));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(2, 1))); //this will be pushed back
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Female));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(1, 3)));
      }
      case "mattia" -> {
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Male));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(3, 4)));
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Female));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(1, 4)));
      }
    }

    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();
    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for PlaceYourWorkersEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) {
      case "davide" -> {
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Male));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 1))); //this is the one that will push
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Female));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 2)));
      }
      case "marco" -> {
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Male));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(2, 1))); //this will be pushed back
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Female));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(1, 3)));
      }
      case "mattia" -> {
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Male));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(3, 4)));
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Female));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(1, 4)));
      }
    }

    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();
    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for PlaceYourWorkersEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    switch (server.getGame().getCurrentPlayer().getNickname()) {
      case "davide" -> {
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Male));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 1))); //this is the one that will push
        davideClient.raise(new WorkerSelectionEvent(davide, Sex.Female));
        davideClient.raise(new SpawnWorkerEvent(davide, new Point(1, 2)));
      }
      case "marco" -> {
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Male));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(2, 1))); //this will be pushed back
        marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Female));
        marcoClient.raise(new SpawnWorkerEvent(marco, new Point(1, 3)));
      }
      case "mattia" -> {
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Male));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(3, 4)));
        mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Female));
        mattiaClient.raise(new SpawnWorkerEvent(mattia, new Point(1, 4)));
      }
    }

    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();
    // wait for BoardUpdateEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for CurrentPlayerChangedEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();

    // wait for GameStartEvent
    davideClient.getServerEvents().take();
    marcoClient.getServerEvents().take();
    mattiaClient.getServerEvents().take();
  }

  void firstTurn() throws InterruptedException {
    for (int i = 0; i < 3; i++) {
      switch (server.getGame().getCurrentPlayer().getNickname()) {
        case "davide" -> {
          davideClient.raise(new WorkerSelectionEvent(davide, Sex.Female));
          davideClient.raise(new MovementEvent(davide, new Point(0, 2)));
          davideClient.raise(new BuildEvent(davide, new Point(0, 1), 1));
          davideClient.raise(new SkipEvent(davide));
        }
        case "marco" -> {
          marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Female));
          marcoClient.raise(new MovementEvent(marco, new Point(0, 3)));
          marcoClient.raise(new BuildEvent(marco, new Point(0, 4), 1));
          marcoClient.raise(new SkipEvent(marco));
        }
        case "mattia" -> {
          mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Male));
          mattiaClient.raise(new MovementEvent(mattia, new Point(4, 4)));
          mattiaClient.raise(new BuildEvent(mattia, new Point(4, 3), 1));
          mattiaClient.raise(new SkipEvent(mattia));
        }
      }

      // wait for CurrentPlayerChangedEvent
      while (!(davideClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
      while (!(marcoClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
      while (!(mattiaClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
    }
  }

  void secondTurn() throws InterruptedException {
    for (int i = 0; i < 3; i++) {
      switch (server.getGame().getCurrentPlayer().getNickname()) {
        case "davide" -> {
          davideClient.raise(new WorkerSelectionEvent(davide, Sex.Male));
          davideClient.raise(new MovementEvent(davide, new Point(2, 1)));
          davideClient.raise(new BuildEvent(davide, new Point(2, 0), 1));
          davideClient.raise(new SkipEvent(davide));
        }
        case "marco" -> {
          marcoClient.raise(new WorkerSelectionEvent(marco, Sex.Female));
          marcoClient.raise(new MovementEvent(marco, new Point(1, 3)));
          marcoClient.raise(new BuildEvent(marco, new Point(0, 3), 1));
          marcoClient.raise(new SkipEvent(marco));
        }
        case "mattia" -> {
          mattiaClient.raise(new WorkerSelectionEvent(mattia, Sex.Male));
          mattiaClient.raise(new MovementEvent(mattia, new Point(3, 3)));
          mattiaClient.raise(new BuildEvent(mattia, new Point(4, 4), 1));
          mattiaClient.raise(new SkipEvent(mattia));
        }
      }

      // wait for CurrentPlayerChangedEvent
      while (!(davideClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
      while (!(marcoClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
      while (!(mattiaClient.getServerEvents().take() instanceof CurrentPlayerChangedEvent));
    }
  }

  @Test
  @Disabled
  void movesWithConsequences() throws InterruptedException, IOException {
    godSelection();

    // TODO: assert god selection went ok

    placeWorkers();

    assertEquals(new Point(1, 1), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 2), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Female)));

    assertEquals(new Point(2, 1), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 3), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Female)));

    assertEquals(new Point(3, 4), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 4), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Female)));

    firstTurn();

    assertEquals(new Point(1, 1), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Male)));
    assertEquals(new Point(0, 2), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Female)));

    assertEquals(new Point(2, 1), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Male)));
    assertEquals(new Point(0, 3), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Female)));

    assertEquals(new Point(4, 4), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 4), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Female)));

    secondTurn();

    assertEquals(new Point(2, 1), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Male)));
    assertEquals(new Point(0, 2), server.getGame().getBoard().getItemPosition(davide.getWorkers().get(Sex.Female)));

    assertEquals(new Point(3, 1), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 3), server.getGame().getBoard().getItemPosition(marco.getWorkers().get(Sex.Female)));

    assertEquals(new Point(3, 3), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Male)));
    assertEquals(new Point(1, 4), server.getGame().getBoard().getItemPosition(mattia.getWorkers().get(Sex.Female)));

  }

  @AfterEach
  void cleanup() throws InterruptedException {
    server.kill();
  }

}
