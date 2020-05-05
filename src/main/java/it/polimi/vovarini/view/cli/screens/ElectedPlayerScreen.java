package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.MultiChoiceList;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.input.Key;

import java.util.List;

public class ElectedPlayerScreen extends Screen {

  private final Text explanation;
  private final MultiChoiceList<GodName> godNameMultiChoiceList;
  private final Text confirmationPrompt;

  public ElectedPlayerScreen(ViewData data, GameClient client, List<GodName> allGods){
    super(data, client);
    explanation = new Text(
            "You are the chosen one. Choose which cards will be available during this game.\n\n"
    );
    godNameMultiChoiceList = new MultiChoiceList<>(allGods, data.getPlayerSet().size());
    confirmationPrompt = new Text("Press O to confirm your choice.");
  }

  private void confirm(){
    System.out.println("ooooi");
    if (godNameMultiChoiceList.maxSelected()) {
      client.raise(new AvailableCardsEvent(data.getOwner(),
              godNameMultiChoiceList.getSelectedOptions().toArray(GodName[]::new)));
    }
  }

  @Override
  public void handleKeyPress(Key key) {
    switch (key){
      case W -> godNameMultiChoiceList.moveUp();
      case S -> godNameMultiChoiceList.moveDown();
      case Spacebar -> godNameMultiChoiceList.select();
      case O -> confirm();
    }
  }

  @Override
  public String render(){
    return explanation.render() +
            godNameMultiChoiceList.render() +
            (godNameMultiChoiceList.maxSelected() ? ("\n" + confirmationPrompt.render()) : "");
  }
}
