package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.AvailableCardsEvent;
import it.polimi.vovarini.common.events.GameEvent;
import it.polimi.vovarini.common.events.GodSelectionStartEvent;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.gui.Gui;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
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
        addImages(Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new));
    }

    public void addImages(GodName[] allGods) {
        // aggiungo le carte alla grafica
        String selector;
        for (int i = 0; i < allGods.length; i++) {

            selector = "#godCard" + i;
            ImageView temp = (ImageView) mainPane.lookup(selector);

            temp.setImage(Settings.godImages.get(allGods[i]));
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

        godCard0.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard0, 0));
        godCard1.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard1, 1));
        godCard2.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard2, 2));
        godCard3.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard3, 3));
        godCard4.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard4, 4));
        godCard5.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard5, 5));
        godCard6.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard6, 6));
        godCard7.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard7, 7));
        godCard8.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard8, 8));
        godCard9.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard9, 9));
        godCard10.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard10, 10));
        godCard11.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard11, 11));
        godCard12.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard12, 12));
        godCard13.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(godCard13, 13));
    }

    private void onCardButtonClick(ImageView godCard, int i) {

        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        String style = godCard.getStyle();
        Image img = godCard.getImage();

        // se già scelta precedentemente, deseleziona carta
        if (selectedCards.contains(godNames[i])) {
            int index = selectedCards.indexOf(godNames[i]);
            selectedCards.remove(godNames[i]);

            switch (index) {
                case 0:
                    //selectedGodCard1.setStyle(selectedGodCard2.getStyle());
                    selectedGodCard1.setImage(null);
                    selectedGodCard1.setImage(selectedGodCard2.getImage());
                    selectedGodCard2.setImage(null);
                    //selectedGodCard2.setStyle(selectedGodCard3.getStyle());
                    selectedGodCard2.setImage(null);
                    selectedGodCard2.setImage(selectedGodCard3.getImage());
                    selectedGodCard3.setImage(null);
                    //selectedGodCard3.setStyle("");
                    selectedGodCard3.setImage(null);
                    break;
                case 1:
                    selectedGodCard2.setImage(null);
                    selectedGodCard2.setStyle(selectedGodCard3.getStyle());
                    selectedGodCard2.setImage(selectedGodCard3.getImage());
                    selectedGodCard3.setImage(null);
                    selectedGodCard3.setStyle("");
                    break;
                case 2:
                    selectedGodCard3.setStyle("");
                    selectedGodCard3.setImage(null);
                    break;
            }
            return;
        }

        // seleziono carta
        switch (selectedCards.size()) {
            case 0:
                selectedCards.add(godNames[i]);
                selectedGodCard1.setStyle(style);
                selectedGodCard1.setImage(img);
                break;
            case 1:
                selectedCards.add(godNames[i]);
                selectedGodCard2.setStyle(style);
                selectedGodCard2.setImage(img);
                break;
            case 2:
                if (GuiManager.getInstance().getNumberOfPlayers() == 3) {
                    selectedCards.add(godNames[i]);
                    selectedGodCard3.setStyle(style);
                    selectedGodCard3.setImage(img);
                }
                break;
            case 3:
            default:
                break;
        }
    }

    private void onMouseEntered(ImageView godCard, int i) {
        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        Tooltip tooltip = new Tooltip();
        tooltip.setText(Settings.descriptions.get(godNames[i]));
        Tooltip.install(godCard, tooltip);
    }

    @FXML
    private void submit(ActionEvent event) {
        if (GuiManager.getInstance().getNumberOfPlayers() == selectedCards.size()) {
            GameEvent evt = new AvailableCardsEvent(GuiManager.getInstance().getData().getOwner(),
                    selectedCards.toArray(GodName[]::new));
            GuiManager.getInstance().getClient().raise(evt);
            GuiManager.getInstance().setCurrentScene(mainPane.getScene());
            GuiManager.getInstance().setLayout(Settings.WAIT_SCENE_FXML);
        }
    }

    @Override
    public void handleGodSelectionStart(GodSelectionStartEvent e) {
        super.handleGodSelectionStart(e);
        addImages(e.getAllGods());
    }
}
