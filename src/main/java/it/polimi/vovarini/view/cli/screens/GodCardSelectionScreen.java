package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.CardChoiceEvent;
import it.polimi.vovarini.common.network.GameClient;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.MultiChoiceList;
import it.polimi.vovarini.view.cli.elements.Text;
import it.polimi.vovarini.view.cli.input.Key;

import java.util.List;

public class GodCardSelectionScreen extends Screen{
  private final MultiChoiceList<GodName> godNameMultiChoiceList;
  private final Text confirmationPrompt;

  public GodCardSelectionScreen(ViewData data, GameClient client, List<GodName> availableGods){
    super(data, client);
    godNameMultiChoiceList = new MultiChoiceList<>(availableGods, 1);
    confirmationPrompt = new Text("Press O to confirm your choice.");
  }

  private void confirm(){
    if (godNameMultiChoiceList.maxSelected()) {
      client.raise(new CardChoiceEvent(data.getOwner(),
              godNameMultiChoiceList.getSelectedOptions().iterator().next()));
      handlesInput = false;
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
    needsRender = true;
  }

  @Override
  public String render(){
    needsRender = false;
    return godNameMultiChoiceList.render() +
            (godNameMultiChoiceList.maxSelected() ? ("\n" + confirmationPrompt.render()) : "");
  }
}
