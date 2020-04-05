package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.Movement;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.BoxEmptyException;
import it.polimi.vovarini.model.board.InvalidPositionException;
import it.polimi.vovarini.model.board.ItemNotFoundException;
import it.polimi.vovarini.model.board.items.Block;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Worker;

import java.util.Arrays;
import java.util.Stack;

public class GodCardFactory {
  public static GodCard create(GodName name){
    switch (name){
      case Apollo: {
        return createApollo();
      }
      case Nobody:
      default: {
        return createNobody();
      }
    }
  }

  private static GodCard createApollo(){
    GodCard apollo = new GodCard(GodName.Apollo);
    return apollo;
  }

  private static GodCard createNobody(){
    return new GodCard(GodName.Nobody);
  }
}
