package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.CardChoiceEvent;
import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class GodCardSelectionController {

    @FXML
    private BorderPane mainPane;

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
        guiManager.setGodCardSelectionController(this);
        bindEvents();
    }

    void addImages(GodName[] availableGodCards, boolean disabled) {
        allGods = availableGodCards;
        String selector;
        for(int i = 0; i < availableGodCards.length; i++) {

            selector = "#godCard" + i;
            Node temp = mainPane.lookup(selector);

            String url = "url('/img/godcards/" + availableGodCards[i].name() + ".png');";
            temp.setStyle("-fx-image: " + url);
            temp.setDisable(disabled);
        }
        changeVisibility(availableGodCards, disabled);
    }

    /**
     * Binds click events
     */
    private void bindEvents() {
        godCard0.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(0));
        godCard1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(1));
        godCard2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(2));
    }

    /**
     *
     * @param cardChosen represent the godCard chosen
     */
    private void onCardButtonClick(int cardChosen) {
        godCard0.setDisable(true);
        godCard1.setDisable(true);
        godCard2.setDisable(true);

        GameEvent evt = new CardChoiceEvent(guiManager.getData().getOwner(), allGods[cardChosen]);
        guiManager.getClient().raise(evt);
    }

    void changeVisibility(GodName[] godsLeft, boolean disabled) {

        godCard0.setDisable(true);
        godCard1.setDisable(true);
        godCard2.setDisable(true);

        for(int k = 0; k < godsLeft.length; k++) {
            for(int i = 0; i < allGods.length; i++) {

                if(allGods[i].equals(godsLeft[k])) {
                    String selector = "#godCard" + i;
                    Node temp = mainPane.lookup(selector);
                    temp.setDisable(disabled);
                }
            }
        }

        if(disabled) {
            instruction.setText("Wait for " + guiManager.getData().getCurrentPlayer().getNickname() + " choice");
        } else {
            instruction.setText("It's your turn, choose your card!");
        }
    }

    void showChoice(Player targetPlayer, GodCard assignedCard) {
        for (int i = 0; i < allGods.length; i++) {
            if (allGods[i].equals(assignedCard.getName())) {
                String selector = "#label" + i;
                Label temp = (Label) mainPane.lookup(selector);
                temp.setText(targetPlayer.getNickname());
            }
        }
    }

    void changeLayout(String path) {
        GuiManager.setLayout(mainPane.getScene(), path);
    }
}