package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.godcards.GodCard;

public interface EventsForViewListener {
  void handleBoardUpdate(BoardUpdateEvent e);

  void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e);

  void handlePhaseUpdate(PhaseUpdateEvent e);

  void handleGameStart(GameStartEvent e);

  void handleNewPlayer(NewPlayerEvent e);

  void handleGodSelectionStart(GodSelectionStartEvent e);

  void handleSelectYourCard(SelectYourCardEvent e);

  void handleCardAssignment(CardAssignmentEvent e);

  void handlePlaceYourWorkers(PlaceYourWorkersEvent e);

  void handlePlayerInfoUpdate(PlayerInfoUpdateEvent e);

  void handleGodCardUpdate(GodCardUpdateEvent e);

  void handleVictory(VictoryEvent e);
}
