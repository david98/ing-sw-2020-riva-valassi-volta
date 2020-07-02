package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.CardAssignmentEvent;
import it.polimi.vovarini.common.events.CardChoiceEvent;
import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.SelectYourCardEvent;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class GodCardSelectionController extends GUIController {

  @FXML
  private ImageView godCard0;

  @FXML
  private ImageView godCard1;

  @FXML
  private ImageView godCard2;

  @FXML
  private Label instruction;

  private GuiManager guiManager;

  private GodName[] allGods;

  @FXML
  public void initialize() {

    guiManager = GuiManager.getInstance();
    bindEvents();
  }

  private void addImages(GodName[] availableGodCards) {
    allGods = availableGodCards;
    String selector;
    for (int i = 0; i < availableGodCards.length; i++) {

      selector = "#godCard" + i;
      ImageView temp = (ImageView) mainPane.lookup(selector);

      temp.setImage(Settings.godImages.get(availableGodCards[i]));
    }
  }

  /**
   *
     * @param cardChosen represent the godCard chosen
     */
    private void onCardButtonClick(int cardChosen) {
        if(guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer())) {
            godCard0.setDisable(true);
            godCard1.setDisable(true);
            godCard2.setDisable(true);

            GameEvent evt = new CardChoiceEvent(guiManager.getData().getOwner(), allGods[cardChosen]);
            guiManager.getClient().raise(evt);
        }
    }

    private void onMouseEntered(ImageView godCard, int i) {
      GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
      for (int k = 0; k < godNames.length; k++) {
        if (godNames[k].equals(allGods[i])) {
          Tooltip tooltip = new Tooltip();
          tooltip.setText(Settings.descriptions.get(godNames[k]));
          Tooltip.install(godCard, tooltip);
        }
      }
    }

  /**
   * Binds click events
   */
  private void bindEvents() {
    godCard0.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(0));
    godCard1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(1));
    godCard2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(2));

    godCard0.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard0, 0));
    godCard1.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard1, 1));
    godCard2.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard2, 2));
  }

  private void changeVisibility(GodName[] godsLeft, boolean disabled) {

    godCard0.setDisable(true);
    godCard1.setDisable(true);
    godCard2.setDisable(true);

    for (int k = 0; k < godsLeft.length; k++) {
      for (int i = 0; i < allGods.length; i++) {

        if (allGods[i].equals(godsLeft[k])) {
          String selector = "#godCard" + i;
          Node temp = mainPane.lookup(selector);
          temp.setDisable(disabled);
        }
      }
    }

    if (disabled) {
      instruction.setText("Wait for " + guiManager.getData().getCurrentPlayer().getNickname() + "'s choice...");
    } else {
      instruction.setText("It's your turn, choose your card!");
    }
  }

  private void showChoice(Player targetPlayer, GodCard assignedCard) {
    for (int i = 0; i < allGods.length; i++) {
      if (allGods[i].equals(assignedCard.getName())) {
        String selector = "#label" + i;
        Label temp = (Label) mainPane.lookup(selector);
        temp.setText(targetPlayer.getNickname());
      }
    }
  }

  @Override
  public void handle(SelectYourCardEvent e) {
    // solo al primo SelectYourCardEvent stampo a video le immagini delle carte,
    // poi disabilito quelle scelte e basta (le img a video restano le stesse)
    if (e.getGodsLeft().length == guiManager.getNumberOfPlayers()) {
      addImages(e.getGodsLeft());
    }

    changeVisibility(e.getGodsLeft(), !e.getTargetPlayer().equals(GuiManager.getInstance().getData().getOwner()));
  }


  @Override
  public void handle(CardAssignmentEvent e) {
    showChoice(e.getTargetPlayer(), e.getAssignedCard());
  }
}