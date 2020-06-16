package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.AvailableCardsEvent;
import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GodSelectionStartEvent;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.gui.Gui;
import it.polimi.vovarini.view.gui.GuiManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ElectedPlayerController extends GUIController {

    @FXML
    private ImageView selectedGodCard1;

    @FXML
    private ImageView selectedGodCard2;

    @FXML
    private ImageView selectedGodCard3;

    @FXML
    private Button submit;

    @FXML
    private ImageView godCard0;

    @FXML
    private ImageView godCard1;

    @FXML
    private ImageView godCard2;

    @FXML
    private ImageView godCard3;

    @FXML
    private ImageView godCard4;

    @FXML
    private ImageView godCard5;

    @FXML
    private ImageView godCard6;

    @FXML
    private ImageView godCard7;

    @FXML
    private ImageView godCard8;

    @FXML
    private ImageView godCard9;

    @FXML
    private ImageView godCard10;

    @FXML
    private ImageView godCard11;

    @FXML
    private ImageView godCard12;

    @FXML
    private ImageView godCard13;

    private final List<GodName> selectedCards = new LinkedList<>();

    @Override
    public void initialize() {
        super.initialize();
        bindEvents();
    }

    public void addImages(GodName[] allGods) {
        // aggiungo le carte alla grafica
        //GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        String selector;
        for (int i = 0; i < allGods.length; i++) {

            selector = "#godCard" + i;
            Node temp = mainPane.lookup(selector);

            String url = "url('/img/godcards/" + allGods[i].name() + ".png');";
            temp.setStyle("-fx-image: " + url);
        }
    }

    /**
     * Binds click events
     */
    private void bindEvents() {
        //ImageView non sono Button, quindi non hanno eventi su click o simili, ergo li aggiungo io
        godCard0.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard0, 0));
        godCard1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard1, 1));
        godCard2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard2, 2));
        godCard3.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard3, 3));
        godCard4.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard4, 4));
        godCard5.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard5, 5));
        godCard6.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard6, 6));
        godCard7.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard7, 7));
        godCard8.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard8, 8));
        godCard9.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard9, 9));
        godCard10.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard10, 10));
        godCard11.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard11, 11));
        godCard12.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard12, 12));
        godCard13.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardButtonClick(godCard13, 13));
    }

    private void onCardButtonClick(ImageView godCard, int i) {

        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        String style = godCard.getStyle();

        // se già scelta precedentemente, deseleziona carta
        if (selectedCards.contains(godNames[i])) {
            int index = selectedCards.indexOf(godNames[i]);
            selectedCards.remove(godNames[i]);

            switch (index) {
                case 0:
                    selectedGodCard1.setStyle(selectedGodCard2.getStyle());
                    selectedGodCard2.setStyle(selectedGodCard3.getStyle());
                    selectedGodCard3.setStyle("");
                    break;
                case 1:
                    selectedGodCard2.setStyle(selectedGodCard3.getStyle());
                    selectedGodCard3.setStyle("");
                    break;
                case 2:
                    selectedGodCard3.setStyle("");
                    break;
            }
            return;
        }

        // seleziono carta
        switch (selectedCards.size()) {
            case 0:
                selectedCards.add(godNames[i]);
                selectedGodCard1.setStyle(style);
                break;
            case 1:
                selectedCards.add(godNames[i]);
                selectedGodCard2.setStyle(style);
                break;
            case 2:
                if (GuiManager.getInstance().getNumberOfPlayers() == 3) {
                    selectedCards.add(godNames[i]);
                    selectedGodCard3.setStyle(style);
                }
                break;
            case 3:
            default:
                break;
        }
    }

    @FXML
    private void submit(ActionEvent event) {
        if (GuiManager.getInstance().getNumberOfPlayers() == selectedCards.size()) {
            GameEvent evt = new AvailableCardsEvent(GuiManager.getInstance().getData().getOwner(),
                    selectedCards.toArray(GodName[]::new));
            GuiManager.getInstance().getClient().raise(evt);
            GuiManager.getInstance().setCurrentScene(mainPane.getScene());
            GuiManager.getInstance().setLayout("/fxml/waitScene.fxml");
        }
    }

    @Override
    public void handleGodSelectionStart(GodSelectionStartEvent e) {
        super.handleGodSelectionStart(e);
        addImages(e.getAllGods());
    }
}
