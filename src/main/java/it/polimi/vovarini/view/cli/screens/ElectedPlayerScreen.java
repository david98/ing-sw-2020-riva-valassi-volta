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


  private final MultiChoiceList<GodName> godNameMultiChoiceList;
  private final Text confirmationPrompt;

  public ElectedPlayerScreen(ViewData data, GameClient client, List<GodName> allGods){
    super(data, client);
    godNameMultiChoiceList = new MultiChoiceList<>(allGods, data.getPlayers().size());
    confirmationPrompt = new Text("Press O to confirm your choice.");
  }

  @Override
  public void handleBoardUpdate(BoardUpdateEvent e) {

  }

  @Override
  public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {

  }

  @Override
  public void handlePhaseUpdate(PhaseUpdateEvent e) {

  }

  @Override
  public void handleGameStart(GameStartEvent e) {

  }

  @Override
  public void handleNewPlayer(NewPlayerEvent e) {

  }

  @Override
  public void handleGodSelectionStart(GodSelectionStartEvent e) {

  }

  @Override
  public void handleSelectYourCard(SelectYourCardEvent e) {

  }

  @Override
  public void handleCardAssignment(CardAssignmentEvent e) {

  }

  @Override
  public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {

  }

  private void confirm(){
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
    return godNameMultiChoiceList.render() +
            (godNameMultiChoiceList.maxSelected() ? ("\n" + confirmationPrompt.render()) : "");
  }
}
