package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;

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

  void handleLoss(LossEvent e);

  void handleAbruptEnd(AbruptEndEvent e);

  void handleFirstPlayer(FirstPlayerEvent e);

  void handleRegistrationStart(RegistrationStartEvent e);

  void handleInvalidNickname(InvalidNicknameEvent e);
}
