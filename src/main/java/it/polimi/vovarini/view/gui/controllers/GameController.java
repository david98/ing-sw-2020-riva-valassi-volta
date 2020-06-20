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

    private Point currentWorkerPosition;

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

        changeVisibility(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
    }

    @FXML
    private void onButtonClick(ImageView img, Point p) {

        switch (currentPhase.getText()) {
            case "START":
                Worker selectedWorker = (Worker) b.getBox(p).getItems().peek();
                guiManager.getData().getOwner().setCurrentSex(selectedWorker.getSex());
                guiManager.getData().getCurrentPlayer().setCurrentSex(selectedWorker.getSex());
                guiManager.getData().setSelectedWorker(selectedWorker);
                guiManager.getData().setCurrentStart(p);
                guiManager.getClient().raise( new WorkerSelectionEvent(guiManager.getData().getOwner(), guiManager.getData().getSelectedWorker().getSex()));
                break;
            case "MOVEMENT":
                break;
            case "CONSTRUCTION":
                break;
            case "END":
                // ripristino tutto (currentSex, currentStart, ecc...) ?
                break;
            default:
                break;
        }
    }

    public void changeVisibility(boolean disabled, String currentPlayer) {

        // aggiorno a video currentPhase
        currentPhase.setText(guiManager.getData().getCurrentPhase().name().toUpperCase());

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                String selector = "#button" + i + j;
                Node node = board.lookup(selector);
                node.setDisable(true);

                Point p = new Point(i,j);
                Item item = b.getBox(p).getItems().peek();

                if(!disabled) {
                    System.out.println("sto modificando la visibilità del current Player");
                    List<Point> reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                    List<Point> buildablePoints = guiManager.getData().getOwner().getGodCard().computeBuildablePoints();

                    switch (currentPhase.getText().toUpperCase()) {
                        case "START":
                            System.out.println("sto modificando dentro start");
                            // abilito solo i worker del currentPlayer che hanno almeno 1 punto raggiungibile
                            if (guiManager.getData().getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
                                guiManager.getData().setSelectedWorker((Worker) item);
                                reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                                node.setDisable(reachablePoints.isEmpty());
                                guiManager.getData().setSelectedWorker(null);
                                System.out.println("sto disabilitando il worker in " + p.toString() + " con " + reachablePoints.isEmpty());
                            }
                            break;
                        case "MOVEMENT":
                            node.setDisable(!reachablePoints.contains(p));
                            //andrebbero evidenziati con qualcosa di grafico
                            break;
                        case "CONSTRUCTION":
                            node.setDisable(!buildablePoints.contains(p));
                            //andrebbero evidenziati con qualcosa di grafico
                            break;
                        case "END":
                            // disabilito tutto?
                            // skippo in automatico?
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        // aggiorno a video CurrentPlayer
        for(int i = 0; i< guiManager.getNumberOfPlayers(); i++) {
            String selector = "#player" + i;
            Label player = (Label) mainPane.lookup(selector);
            if(currentPlayer.equals(guiManager.getData().getPlayers()[i].getNickname())) {
                player.setStyle("-fx-effect: dropshadow(gaussian, #f44336, 15, 0.2, 0, 0);");
            } else {
                player.setStyle("");
            }
        }

        // aggiorno a video il messaggio in alto
        if(disabled) {
            instruction.setText("It's " + currentPlayer + "'s turn");
        } else {
            instruction.setText("It's your turn!");
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
        changeVisibility(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
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
