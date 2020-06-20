package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SpawnWorkerController extends GUIController {

    @FXML
    private GridPane board;

    @FXML
    private Label instruction;

    private GuiManager guiManager;

    private final List<Sex> sexes = new LinkedList<>(Arrays.asList(Sex.values()));

    @FXML
    public void initialize() {

        guiManager = GuiManager.getInstance();
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

                img.setImage(new Image(GameController.class.getResource("/img/workers/free.png").toExternalForm()));

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
            ImageView godCard = (ImageView) mainPane.lookup(selector);

            godCard.setImage(Settings.godImages.get(players[i].getGodCard().getName()));
        }
    }

    @FXML
    public void onGridClick(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();

        System.out.println(event.getPickResult().getIntersectedTexCoord());

        System.out.println(clickedNode.getId());
        System.out.println(event.getPickResult().getIntersectedPoint());
        System.out.println(((Node) event.getSource()).getId());

        Node source = (Node) event.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.println("Mouse clicked cell: " + colIndex + "And: " + rowIndex);


        if (clickedNode != board) {
            // click on descendant node
            Node parent = clickedNode.getParent();
            while (parent != board) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }

            // click on descendant node
            //Integer colIndex = GridPane.getColumnIndex(clickedNode);
            //Integer rowIndex = GridPane.getRowIndex(clickedNode);
            //System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
        }
    }

    @FXML
    private void onButtonClick(ImageView img, Point p) {
        sexes.remove(0);
        guiManager.getClient().raise(new SpawnWorkerEvent(guiManager.getData().getOwner(), p));
    }

    public void updateView(boolean disabled, String currentPlayer) {
        Board b = guiManager.getData().getBoard();
        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                String selector = "#button" + i + j;
                Node node = board.lookup(selector);
                node.setDisable(disabled);

                Point p = new Point(i,j);
                if (b.getBox(p).getItems().peek() != null) {
                    node.setDisable(true);
                }
            }
        }

        for(int i = 0; i< guiManager.getNumberOfPlayers(); i++) {
            String selector = "#player" + i;
            Label player = (Label) mainPane.lookup(selector);
            if(currentPlayer.equals(guiManager.getData().getPlayers()[i].getNickname())) {
                player.setStyle("-fx-effect: innershadow(gaussian, #f44336, 15, 0.2, 0, 0);");
            } else {
                player.setStyle("");
            }
        }

        if(disabled) {
            instruction.setText("Wait for " + currentPlayer + "\n to place him workers...");
        } else if(!sexes.isEmpty()){
            instruction.setText("Place your " + sexes.get(0).toString() + " worker.");
            guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(), sexes.get(0)));
        }
    }

    @Override
    public void handleBoardUpdate(BoardUpdateEvent e) {
        Board b = guiManager.getData().getBoard();

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Point p = new Point(i, j);
                if (b.getBox(p).getItems().peek() != null) {
                    String selector = "#button" + i + j;
                    ImageView img = (ImageView) board.lookup(selector);

                    for(int k = 0; k < guiManager.getNumberOfPlayers(); k++) {
                        if(guiManager.getData().getPlayers()[k].getWorkers().values().stream().anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
                            img.setImage(
                                    Settings.workersImages[k].get(((Worker) b.getBox(p).getItems().peek()).getSex()));
                        }
                    }
                    img.setDisable(true);
                }
            }
        }
        updateView(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()), guiManager.getData().getCurrentPlayer().getNickname());
    }

    @Override
    public void handlePlaceYourWorkers(PlaceYourWorkersEvent e) {
        super.handlePlaceYourWorkers(e);
        updateView(!e.getTargetPlayer().equals(GuiManager.getInstance().getData().getOwner()), e.getTargetPlayer().getNickname());
    }

    @Override
    public void handleCurrentPlayerUpdate(CurrentPlayerChangedEvent e) {
        super.handleCurrentPlayerUpdate(e);
    }
}
