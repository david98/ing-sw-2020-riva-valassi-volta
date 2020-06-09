package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.common.events.SpawnWorkerEvent;
import it.polimi.vovarini.common.events.WorkerSelectionEvent;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Sex;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SpawnWorkerController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private GridPane board;

    @FXML
    private Label instruction;

    private GuiManager guiManager;

    private final List<Sex> sexes = new LinkedList<>(Arrays.asList(Sex.values()));

    private Board b;

    @FXML
    public void initialize() {

        guiManager = GuiManager.getInstance();
        guiManager.setSpawnWorkerController(this);
        bindEvents();
        b = new Board(Board.DEFAULT_SIZE);
    }

    void addImages(Player[] players) {
        String selector;
        for(int i = 0; i < players.length; i++) {

            selector = "#player" + i;
            Label label = (Label) mainPane.lookup(selector);
            label.setText(players[i].getNickname());

            selector = "#godCard" + i;
            Node godCard = mainPane.lookup(selector);

            String url = "url('/img/godcards/" + players[i].getGodCard().getName() + ".png');";
            godCard.setStyle("-fx-image: " + url);
        }
    }

    /**
     * Binds click events
     */
    private void bindEvents() {

        for(int i = 0; i < 5; i++) {
            String selector = "#button" + i + "0";
            ImageView img = (ImageView) board.lookup(selector);

            String id = img.getId();
            int x = Integer.parseInt(id.substring(id.length()-2, id.length()-1));
            int y = Integer.parseInt(id.substring(id.length()-1));

            String url = "url('/img/prova/minecraft.png');";
            img.setStyle("-fx-image: " + url);

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onButtonClick(img, new Point(x,y)));
        }
    }

    @FXML
    private void onButtonClick(ImageView img, Point p) {
        guiManager.getClient().raise(new SpawnWorkerEvent(guiManager.getData().getOwner(), p));
    }

    void boardUpdate() {
        b = guiManager.getData().getBoard();

        if(guiManager.getData().getCurrentPlayer().equals(guiManager.getData().getOwner())) {
            if(sexes.size() > 0)
                sexes.remove(0);
        }

        for(int i = 0; i < b.getSize(); i++) {
            Point p = new Point(i,0);
            if(b.getBox(p).getItems().peek() != null) {
                String selector = "#button" + i + "0";
                ImageView img = (ImageView) board.lookup(selector);
                String url = "url('/img/prova/worker" + b.getBox(p).getItems().peek().toString() + ".png');";
                img.setStyle("-fx-image: " + url);
                img.setDisable(true);
            }
        }

        changeVisibility(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
    }

    void changeVisibility(boolean disabled, String currentPlayer) {
        for (int i = 0; i < 5; i++) {
            String selector = "#button" + i + "0";
            Node node = board.lookup(selector);
            node.setDisable(disabled);

            if(b.getBox(new Point(i,0)).getItems().peek() != null) {
                node.setDisable(true);
            }
        }

        if(disabled) {
            instruction.setText("Wait for " + currentPlayer + " to place him workers...");
        } else {
            instruction.setText("Place your " + sexes.get(0).toString() + " worker.");
            guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(), sexes.get(0)));
        }
    }
}
