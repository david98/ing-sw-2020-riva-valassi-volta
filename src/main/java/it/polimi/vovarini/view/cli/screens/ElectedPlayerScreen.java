package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.MultiChoiceList;

import java.util.List;

public class ElectedPlayerScreen extends Screen {


  private final MultiChoiceList<GodName> godNameMultiChoiceList;

  public ElectedPlayerScreen(ViewData data, List<GodName> allGods){
    super(data);
    godNameMultiChoiceList = new MultiChoiceList<>(allGods, data.getPlayers().size());
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

  @Override
  public void handleKeyPress(int keycode) {

  }

  @Override
  public String render() {
    return null;
  }
}
