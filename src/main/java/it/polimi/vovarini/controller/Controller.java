package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.BuildEvent;
import it.polimi.vovarini.controller.events.CardAssignmentEvent;
import it.polimi.vovarini.controller.events.CardChoiceEvent;
import it.polimi.vovarini.controller.events.WorkerEvent;
import it.polimi.vovarini.model.*;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.InvalidLevelException;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;

import java.util.*;

public class Controller implements EventListener {
  private Game game;
  private ArrayList<GodName> selectedCards;

  // Scelta del numero di giocatori fatta su Server, Game precedentemente istanziato
  public Controller(Game game) {
    this.game = game;
    selectedCards = new ArrayList<GodName>();
  }

  public void update(WorkerEvent evt) throws InvalidPhaseException {
    Phase currentPhase = game.getCurrentPhase();

    if (!currentPhase.equals(Phase.Movement)) throw new InvalidPhaseException();
    game.getCurrentPlayer().setCurrentSex(evt.getSex());
  }

  public void update(BuildEvent evt)
      throws InvalidPositionException, InvalidPhaseException, NonBuildablePositionException {
    Point input = new Point(evt.getBuildEnd());
    if (!input.isValidPoint()) throw new InvalidPositionException();

    Phase currentPhase = game.getCurrentPhase();
    if (!currentPhase.equals(Phase.Construction)) throw new InvalidPhaseException();

    boolean pointFound = false;
    Block blockToBuild = null;
    int levelToBuild = 0;
    for (Point point : game.getCurrentPlayer().getGodCard().computeBuildablePoints()) {
      if (point.equals(input)) {
        pointFound = true;
        try {
          Stack<Item> currentStack = game.getBoard().getBox(point).getItems();
          if (currentStack.peek() instanceof Worker) currentStack.pop();
          if (currentStack.empty()) levelToBuild = 1;
          else {
            Block topBlock = (Block) currentStack.pop();
            levelToBuild = topBlock.getLevel() + 1;
          }
        } catch (BoxEmptyException e) {
          levelToBuild = 1;
        } finally {
          try {
            blockToBuild = new Block(levelToBuild);
          } catch (InvalidLevelException ignored) {
          }
        }
        Move newMove = new Construction(game.getBoard(), blockToBuild, point);
        newMove.execute();
        break;
      }
    }

    if (!pointFound) {
      throw new NonBuildablePositionException();
    }
  }

  public void update(CardChoiceEvent evt) {
    for (GodName card : evt.getSelectedCards()) {
      this.selectedCards.add(card);
    }
  }

  public void update(CardAssignmentEvent evt) throws CardsNotSelectedException, InvalidCardException {
    if (this.selectedCards.isEmpty()) {
      throw new CardsNotSelectedException();
    } else {
        if(!this.selectedCards.contains(evt.getAssignedCard())){
          throw new InvalidCardException();
        }

      //GodCard playerCard = new GodCard(game, evt.getAssignedCard()); (new card instance)
      //game.getCurrentPlayer().setGodCard(playerCard);
    }
  }

  public static void main(String[] args) {}
}
