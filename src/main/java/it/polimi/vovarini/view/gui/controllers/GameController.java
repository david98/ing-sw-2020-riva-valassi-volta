package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Item;
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GameController extends GUIController {

    @FXML
    private GridPane board;

    @FXML
    private Label instruction;

    @FXML
    private Label currentPhase;

    private GuiManager guiManager;

    private static final List<Point> allPoints = new LinkedList<>();

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();

        for (int i = 0; i < guiManager.getData().getBoard().getSize(); i++) {
            for (int j = 0; j < guiManager.getData().getBoard().getSize(); j++) {
                allPoints.add(new Point(i, j));
            }
        }

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
                img.setImage(Settings.freeImage);

                // needed because the event handler needs final variables
                final int x = i;
                final int y = j;

                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onButtonClick(img, new Point(x, y)));
                img.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(img, new Point(x, y)));
                img.addEventHandler(MouseEvent.MOUSE_EXITED, event -> onMouseExited(img, new Point(x, y)));
            }
        }
    }

    @FXML
    private void onMouseEntered(ImageView img, Point p) {
        Board b = guiManager.getData().getBoard();
        if (guiManager.getData().getOwner().getWorkers()
                .values()
                .stream()
                .anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
            var worker = (Worker) b.getBox(p).getItems().peek();
            guiManager.getData().getOwner().setCurrentSex(worker.getSex());
            List<Point> reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
            highlightPoints(reachablePoints);
        }
    }

    private void highlightPoints(Collection<Point> points) {
        for (Point point: points) {
            String selector = "#button" + point.getX() + point.getY();
            ImageView imageView = (ImageView) board.lookup(selector);
            System.out.println("Highlighting " + point.toString());
            imageView.setStyle("-fx-effect: innershadow(gaussian, #008000, 15, 0.2, 0, 0);");
        }
    }

    @FXML
    private void onMouseExited(ImageView img, Point p) {
        deHighlightPoints(allPoints);
    }

    private void deHighlightPoints(Collection<Point> points) {
        for (Point point: points) {
            String selector = "#button" + point.getX() + point.getY();
            ImageView imageView = (ImageView) board.lookup(selector);
            System.out.println("Highlighting " + point.toString());
            imageView.setStyle("");
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

            String url = "url('" + GameController.class.getResource("/img/godcards/" +
                    players[i].getGodCard().getName() + ".png").toExternalForm() + "');";
            godCard.setStyle("-fx-image: " + url);
        }

        Board b = guiManager.getData().getBoard();

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Point p = new Point(i, j);
                if (b.getBox(p).getItems().peek() != null) {
                    selector = "#button" + i + j;
                    ImageView img = (ImageView) board.lookup(selector);

                    for(int k = 0; k < guiManager.getNumberOfPlayers(); k++) {
                        if(guiManager.getData().getPlayers()[k].getWorkers()
                                .values()
                                .stream()
                                .anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
                            img.setImage(Settings.workersImages[k].get(((Worker)b.getBox(p).getItems().peek()).getSex()));
                        }
                    }

                    img.setDisable(true);
                }
            }
        }

        updateView();
    }

    @FXML
    private void onButtonClick(ImageView img, Point p) {
        Board b = guiManager.getData().getBoard();

        switch (guiManager.getData().getCurrentPhase()) {
            case Start -> {
                Worker selectedWorker = (Worker) b.getBox(p).getItems().peek();
                if (selectedWorker != null) {
                    guiManager.getData().getOwner().setCurrentSex(selectedWorker.getSex());
                    guiManager.getData().getCurrentPlayer().setCurrentSex(selectedWorker.getSex());
                    guiManager.getData().setSelectedWorker(selectedWorker);
                    guiManager.getData().setCurrentStart(p);
                    guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(),
                            guiManager.getData().getSelectedWorker().getSex()));
                }
            }
            case Movement ->
                guiManager.getClient().raise(new MovementEvent(guiManager.getData().getOwner(), p));
            case Construction -> {
                int levelToBuild = b.getBox(p).getLevel() + 1;
                guiManager.getClient().raise(new BuildEvent(guiManager.getData().getOwner(), p, levelToBuild));
            }
            case End -> {
                // ripristino tutto (currentSex, currentStart, ecc...) ?
            }
            default -> {}
        }
    }

    public void updateView() {

        // aggiorno a video currentPhase
        currentPhase.setText(guiManager.getData().getCurrentPhase().name().toUpperCase());
        Board b = guiManager.getData().getBoard();
        List<Point> reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
        List<Point> buildablePoints = guiManager.getData().getOwner().getGodCard().computeBuildablePoints();

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                String selector = "#button" + i + j;
                Node node = board.lookup(selector);
                node.setDisable(true);

                Point p = new Point(i,j);
                Item item = b.getBox(p).getItems().peek();

                if(guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer())) {
                    switch (guiManager.getData().getCurrentPhase()) {
                        case Start -> {
                            // abilito solo i worker del currentPlayer che hanno almeno 1 punto raggiungibile
                            if (guiManager.getData().getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
                                guiManager.getData().setSelectedWorker((Worker) item);
                                reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                                node.setDisable(reachablePoints.isEmpty());
                                guiManager.getData().setSelectedWorker(null);
                            }
                        }
                        case Movement -> {
                            highlightPoints(reachablePoints);
                            node.setDisable(!reachablePoints.contains(p));
                        }
                            //andrebbero evidenziati con qualcosa di grafico
                        case Construction -> {
                            deHighlightPoints(allPoints);
                            highlightPoints(buildablePoints);
                            node.setDisable(!buildablePoints.contains(p));
                        }
                            //andrebbero evidenziati con qualcosa di grafico
                        case End -> {
                            deHighlightPoints(allPoints);
                            guiManager.getClient().raise(new SkipEvent(guiManager.getData().getOwner()));
                            // disabilito tutto?
                            // skippo in automatico?
                        }
                        default -> {}
                    }
                }
            }
        }

        // aggiorno a video CurrentPlayer
        for(int i = 0; i< guiManager.getNumberOfPlayers(); i++) {
            String selector = "#player" + i;
            Label player = (Label) mainPane.lookup(selector);
            if(guiManager.getData().getCurrentPlayer().equals(guiManager.getData().getPlayers()[i])) {
                player.setStyle("-fx-effect: dropshadow(gaussian, #f44336, 15, 0.2, 0, 0);");
            } else {
                player.setStyle("");
            }
        }

        // aggiorno a video il messaggio in alto
        if(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer())) {
            instruction.setText("It's " + guiManager.getData().getCurrentPlayer().getNickname() + "'s turn");
        } else {
            instruction.setText("It's your turn!");
        }
    }

    @Override
    public void handleBoardUpdate(BoardUpdateEvent e) {
        Board b = guiManager.getData().getBoard();
        Point p;
        String selector, url;
        ImageView button, level;

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                p = new Point(i, j);
                selector = "#button" + i + j;
                button = (ImageView) board.lookup(selector);
                selector = "#level" + i + j;
                level = (ImageView) board.lookup(selector);

                level.setImage(null);
                button.setImage(null);

                Item item = b.getBox(p).getItems().peek();

                if(item == null) {
                    level.setImage(Settings.freeImage);
                } else {
                    if (b.getBox(p).getLevel() != 0) {
                        level.setImage(Settings.levelsImages[b.getBox(p).getLevel()]);
                    }

                    if(item.canBeRemoved()) {
                        for(int k = 0; k < guiManager.getNumberOfPlayers(); k++) {
                            if(guiManager.getData().getPlayers()[k].getWorkers()
                                    .values()
                                    .stream()
                                    .anyMatch(w -> w.equals(item))) {
                                button.setImage(Settings.workersImages[k].get(((Worker)item).getSex()));
                            }
                        }
                    }

                }
            }
        }
        updateView();
    }

    @Override
    public void handlePhaseUpdate(PhaseUpdateEvent e) {
        super.handlePhaseUpdate(e);
        updateView();
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
