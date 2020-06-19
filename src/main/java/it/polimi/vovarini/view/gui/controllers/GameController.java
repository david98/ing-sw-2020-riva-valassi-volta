package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Item;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.view.gui.GuiManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameController extends GUIController {

    @FXML
    private GridPane board;

    @FXML
    private Label instruction;

    @FXML
    private Label currentPhase;

    private GuiManager guiManager;

    private Board b = new Board(Board.DEFAULT_SIZE);

    @FXML
    public void initialize() {

        guiManager = GuiManager.getInstance();
        b = guiManager.getData().getBoard();
        bindEvents();
        addImages(guiManager.getData().getPlayers());
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

    public void addImages(Player[] players) {
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

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Point p = new Point(i, j);
                if (b.getBox(p).getItems().peek() != null) {
                    selector = "#button" + i + j;
                    ImageView img = (ImageView) board.lookup(selector);
                    String url = "url('/img/prova/worker" + b.getBox(p).getItems().peek().toString() + ".png');";
                    img.setStyle("-fx-image: " + url);
                    img.setDisable(true);
                }
            }
        }

        currentPhase.setText(guiManager.getData().getCurrentPhase().name().toUpperCase());

        changeVisibility(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
    }

    @FXML
    private void onButtonClick(ImageView img, Point p) {
        if(currentPhase.getText().equals(Phase.Start.name().toUpperCase())) {
            //controllare che la lista di reachablePoints sia NON vuota
            Item item = b.getBox(p).getItems().peek();
            guiManager.getData().getOwner().setCurrentSex(((Worker) item).getSex());
            guiManager.getData().getCurrentPlayer().setCurrentSex(((Worker) item).getSex());
            guiManager.getClient().raise( new WorkerSelectionEvent(guiManager.getData().getOwner(), guiManager.getData().getSelectedWorker().getSex()));
        }
    }

    public void changeVisibility(boolean disabled, String currentPlayer) {
        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                String selector = "#button" + i + j;
                Node node = board.lookup(selector);
                node.setDisable(disabled);

                Point p = new Point(i,j);
                Item item = b.getBox(p).getItems().peek();

                if (guiManager.getData().getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
                    node.setDisable(disabled);
                } else {
                    node.setDisable(true);
                }
            }
        }

        for(int i = 0; i< guiManager.getNumberOfPlayers(); i++) {
            String selector = "#player" + i;
            Label player = (Label) mainPane.lookup(selector);
            if(currentPlayer.equals(guiManager.getData().getPlayers()[i].getNickname())) {
                player.setStyle("-fx-effect: dropshadow(gaussian, #f44336, 15, 0.2, 0, 0);");
            } else {
                player.setStyle("");
            }
        }

        if(disabled) {
            instruction.setText("Wait for " + currentPlayer + "...");
       } else {
            instruction.setText("It's your turn!");
            //guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(), sexes.get(0)));
        }
    }

    @Override
    public void handleBoardUpdate(BoardUpdateEvent e) {
        b = e.getNewBoard();

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

    @Override
    public void handlePhaseUpdate(PhaseUpdateEvent e) {
        super.handlePhaseUpdate(e);
        currentPhase.setText("Current phase: " + e.getNewPhase().name().toUpperCase());
    }

    @Override
    public void handleLoss(LossEvent e) {
        super.handleLoss(e);
    }

    @Override
    public void handleVictory(VictoryEvent e) {
        super.handleVictory(e);
    }
}
