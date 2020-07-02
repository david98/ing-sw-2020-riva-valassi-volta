package it.polimi.vovarini.view;

import it.polimi.vovarini.common.events.*;

/**
 * This interface contains all the event listeners
 * that any kind of view should implement.
 *
 * @author Davide Volta
 */
public interface EventsForViewListener {
  void handle(BoardUpdateEvent e);

  void handle(CurrentPlayerChangedEvent e);

  void handle(PhaseUpdateEvent e);

  void handle(GameStartEvent e);

  void handle(NewPlayerEvent e);

  void handle(GodSelectionStartEvent e);

  void handle(SelectYourCardEvent e);

  void handle(CardAssignmentEvent e);

  void handle(PlaceYourWorkersEvent e);

  void handle(PlayerInfoUpdateEvent e);

  void handle(GodCardUpdateEvent e);

  void handle(VictoryEvent e);

  void handle(LossEvent e);

  void handle(AbruptEndEvent e);

  void handle(FirstPlayerEvent e);

  void handle(RegistrationStartEvent e);

  void handle(InvalidNicknameEvent e);
}
