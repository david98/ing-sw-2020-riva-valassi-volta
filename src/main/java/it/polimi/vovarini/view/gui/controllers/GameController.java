package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Phase;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Collection;
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
                ImageView button = (ImageView) board.lookup(selector);
                // needed because the event handler needs final variables
                final int x = i;
                final int y = j;

                button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onMouseEntered(new Point(x, y)));
                button.addEventHandler(MouseEvent.MOUSE_EXITED, event -> onMouseExited());
            }
        }
    }

    private void onMouseEntered(Point p) {

        if(guiManager.getData().getCurrentPhase().equals(Phase.Start)) {
            Board b = guiManager.getData().getBoard();
            if (guiManager.getData().getOwner().getWorkers()
                    .values()
                    .stream()
                    .anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
                var worker = (Worker) b.getBox(p).getItems().peek();
                guiManager.getData().getCurrentPlayer().setCurrentSex(worker.getSex());
                List<Point> reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                highlightPoints(reachablePoints);
            }
        }
    }

    private void highlightPoints(Collection<Point> points) {


        for (Point point: points) {

            String selector = "#level" + point.getX() + point.getY();
            Pane level = (Pane) board.lookup(selector);

            if(guiManager.getData().getBoard().getBox(point).getItems().size() == 0) {
                level.setStyle("-fx-background-color: rgba(255, 0, 0, 0.2)");
            } else {
                level.setStyle("-fx-effect: dropshadow(gaussian, #F44336, 15, 0.2, 0, 0);");
            }
        }
    }

    @FXML
    private void onMouseExited() {
        if(guiManager.getData().getCurrentPhase().equals(Phase.Start)) {
            deHighlightPoints(allPoints);
        }
    }

    private void deHighlightPoints(Collection<Point> points) {
        for (Point point: points) {
            String selector = "#level" + point.getX() + point.getY();
            Pane level = (Pane) board.lookup(selector);
            level.setStyle("");
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

        Board b = guiManager.getData().getBoard();

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Point p = new Point(i, j);
                if (b.getBox(p).getItems().peek() != null) {
                    selector = "#button" + i + j;
                    ImageView button = (ImageView) board.lookup(selector);

                    for(int k = 0; k < guiManager.getNumberOfPlayers(); k++) {
                        if(guiManager.getData().getPlayers()[k].getWorkers()
                                .values()
                                .stream()
                                .anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
                            button.setImage(Settings.workersImages[k].get(((Worker)b.getBox(p).getItems().peek()).getSex()));
                        }
                    }
                }
            }
        }

        // new BackgroundSize(width, height, widthAsPercentage, heightAsPercentage, contain, cover)
        BackgroundSize backgroundSize = new BackgroundSize(500, 500, false, false, true, false);
        // new BackgroundImage(image, repeatX, repeatY, position, size)
        BackgroundImage backgroundImage = new BackgroundImage(Settings.bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        board.setBackground(background);

        updateView();
    }

    @FXML
    public void onGridClick(MouseEvent event) {

        Node clickedNode = event.getPickResult().getIntersectedNode();

        Integer x = GridPane.getColumnIndex(clickedNode);
        Integer y = GridPane.getRowIndex(clickedNode);

        gameAction(new Point(x,y));
    }

    private void gameAction(Point p) {
        Board b = guiManager.getData().getBoard();

        switch (guiManager.getData().getCurrentPhase()) {
            case Start -> {
                Worker selectedWorker = (Worker) b.getBox(p).getItems().peek();
                if (selectedWorker != null) {
                    //guiManager.getData().getOwner().setCurrentSex(selectedWorker.getSex());
                    guiManager.getData().getCurrentPlayer().setCurrentSex(selectedWorker.getSex());
                    guiManager.getData().setSelectedWorker(selectedWorker);
                    //guiManager.getData().setCurrentStart(p);
                    guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(),
                            guiManager.getData().getSelectedWorker().getSex()));
                }
            }
            case Movement -> {
                List<Point> reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                if (reachablePoints.contains(p)) {
                    guiManager.getClient().raise(new MovementEvent(guiManager.getData().getOwner(), p));
                    // aggiungere spostamento automatico prima della risposta del server
                }
            }
            case Construction -> {
                List<Point> buildablePoints = guiManager.getData().getOwner().getGodCard().computeBuildablePoints();
                if(buildablePoints.contains(p)) {
                    int levelToBuild = b.getBox(p).getLevel() + 1;
                    guiManager.getClient().raise(new BuildEvent(guiManager.getData().getOwner(), p, levelToBuild));
                    // aggiungere costruzione automatica prima della risposta del server
                }
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
        board.setDisable(!guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer()));

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {

                Point p = new Point(i,j);
                Item item = b.getBox(p).getItems().peek();

                if(guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer())) {
                    switch (guiManager.getData().getCurrentPhase()) {
                        case Start -> {
                            // abilito solo i worker del currentPlayer che hanno almeno 1 punto raggiungibile
                            if (guiManager.getData().getOwner().getWorkers().values().stream().anyMatch(w -> w.equals(item))) {
                                guiManager.getData().setSelectedWorker((Worker) item);
                                reachablePoints = guiManager.getData().getOwner().getGodCard().computeReachablePoints();
                                guiManager.getData().setSelectedWorker(null);
                            }
                        }
                        case Movement -> {
                            deHighlightPoints(allPoints);
                            highlightPoints(reachablePoints);
                        }
                        case Construction -> {
                            deHighlightPoints(allPoints);
                            highlightPoints(buildablePoints);
                        }
                        case End -> {
                            deHighlightPoints(allPoints);
                            // skippo in automatico? NO, altrimenti arriva l'evento anche dal nemico (server si incazza)
                            // se sono in End devo premere a caso sulla grid per skippare (da risolvere)
                            //guiManager.getClient().raise(new SkipEvent(guiManager.getData().getOwner()));
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
        String selector;
        ImageView button;
        Pane level;

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                p = new Point(i, j);
                selector = "#button" + i + j;
                button = (ImageView) board.lookup(selector);
                selector = "#level" + i + j;
                level = (Pane) board.lookup(selector);

                button.setImage(null);

                Item item = b.getBox(p).getItems().peek();

                if(item != null) {
                    if (b.getBox(p).getLevel() != 0) {

                        // new BackgroundSize(width, height, widthAsPercentage, heightAsPercentage, contain, cover)
                        BackgroundSize backgroundSize = new BackgroundSize(100, 100, false, false, true, false);
                        // new BackgroundImage(image, repeatX, repeatY, position, size)
                        BackgroundImage backgroundImage = new BackgroundImage(Settings.levelsImages[b.getBox(p).getLevel()-1], BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                        // new Background(images...)
                        Background background = new Background(backgroundImage);

                        level.setBackground(background);


                        //ImageView img = new ImageView(Settings.levelsImages[b.getBox(p).getLevel()-1]);
                        //img.setStyle("-fx-max-width: 100; -fx-max-height: 100");
                        //level.getChildren().add(img);
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
        //skip automatico
        if(e.getNewPhase().equals(Phase.End)) {
            if(guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer())) {
                guiManager.getClient().raise(new SkipEvent(guiManager.getData().getOwner()));
            }
        }
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
