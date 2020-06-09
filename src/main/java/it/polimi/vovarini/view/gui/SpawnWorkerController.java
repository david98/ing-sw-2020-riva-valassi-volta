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

    private Board b = new Board(Board.DEFAULT_SIZE);

    @FXML
    public void initialize() {

        guiManager = GuiManager.getInstance();
        guiManager.setSpawnWorkerController(this);
        bindEvents();
    }

    /**
     * Binds click events
     */
    private void bindEvents() {

        for(int i = 0; i < Board.DEFAULT_SIZE; i++) {
            for(int j = 0; j < Board.DEFAULT_SIZE; j++) {
                String selector = "#button" + i + j;
                ImageView img = (ImageView) board.lookup(selector);

                String url = "url('/img/prova/minecraft.png');";
                img.setStyle("-fx-image: " + url);

                int x = i;
                int y = j;

                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onButtonClick(img, new Point(x, y)));
            }
        }
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

    @FXML
    private void onButtonClick(ImageView img, Point p) {
        sexes.remove(0);
        guiManager.getClient().raise(new SpawnWorkerEvent(guiManager.getData().getOwner(), p));
    }

    void boardUpdate() {
        b = guiManager.getData().getBoard();

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Point p = new Point(i, j);
                if (b.getBox(p).getItems().peek() != null) {
                    String selector = "#button" + i + j;
                    ImageView img = (ImageView) board.lookup(selector);
                    String url = "url('/img/prova/worker" + b.getBox(p).getItems().peek().toString() + ".png');";
                    img.setStyle("-fx-image: " + url);
                    img.setDisable(true);
                }
            }
        }
        changeVisibility(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
    }

    void changeVisibility(boolean disabled, String currentPlayer) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < b.getSize(); j++) {
                String selector = "#button" + i + j;
                Node node = board.lookup(selector);
                node.setDisable(disabled);

                if (b.getBox(new Point(i, j)).getItems().peek() != null) {
                    node.setDisable(true);
                }
            }
        }

        for(int i = 0; i< guiManager.getData().getPlayers().length; i++) {
            String selector = "#player" + i;
            Label player = (Label) mainPane.lookup(selector);
            if(currentPlayer.equals(guiManager.getData().getPlayers()[i].getNickname())) {
                player.setStyle("-fx-effect: dropshadow(gaussian, #f44336, 15, 0.2, 0, 0);");
            } else {
                player.setStyle("");
            }
        }

        if(disabled) {
            instruction.setText("Wait for " + currentPlayer + "\n to place him workers...");
        } else {
            instruction.setText("Place your " + sexes.get(0).toString() + " worker.");
            guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(), sexes.get(0)));
        }
    }
}
