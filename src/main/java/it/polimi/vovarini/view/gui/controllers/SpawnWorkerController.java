package it.polimi.vovarini.view.gui.controllers;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.model.Player;
import it.polimi.vovarini.model.Point;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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

  @FXML
  private ImageView godCard0;

  @FXML
  private ImageView godCard1;

  @FXML
  private ImageView godCard2;

  private GuiManager guiManager;

  private final List<Sex> sexes = new LinkedList<>(Arrays.asList(Sex.values()));

  @FXML
  public void initialize() {

    guiManager = GuiManager.getInstance();
    System.out.println(guiManager.getCurrentScene().getRoot().getStyle());
    Player[] players = guiManager.getData().getPlayers();

    for (int i = 0; i < players.length; i++) {
      if (guiManager.getData().getOwner().equals(guiManager.getData().getPlayers()[i])) {
        board.setStyle("-worker-img: url('/img/workers/" + i +
                (sexes.get(0).equals(Sex.Male) ? "M" : "F") + ".png');");
      }
    }
    bindEvents();
    addImages(guiManager.getData().getPlayers());

  }

  public void bindEvents() {
    godCard0.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onGodCardEntered(godCard0, 0));
    godCard1.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onGodCardEntered(godCard1, 1));
    godCard2.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> onGodCardEntered(godCard2, 2));
  }

  private void onGodCardEntered(ImageView godCard, int i) {
    GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
    Tooltip tooltip = new Tooltip();
    tooltip.setText(Settings.descriptions.get(godNames[i]));
    Tooltip.install(godCard, tooltip);
  }

  public void addImages(Player[] players) {
    String selector;
    for (int i = 0; i < players.length; i++) {
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

    Integer x = GridPane.getColumnIndex(clickedNode);
    Integer y = GridPane.getRowIndex(clickedNode);

    Point p = new Point(x, y);

    // piazzo il worker solo se il box è libero
    if (guiManager.getData().getBoard().getBox(p).getItems().peek() == null) {
      spawnWorker(p);
    }

  }

  private void spawnWorker(Point p) {
    sexes.remove(0);
    guiManager.getClient().raise(new SpawnWorkerEvent(guiManager.getData().getOwner(), p));
  }

  public void updateView() {

    boolean disabled = !guiManager.getData().getOwner().equals(guiManager.getData().getCurrentPlayer());

    board.setDisable(disabled);

    for (int i = 0; i < guiManager.getNumberOfPlayers(); i++) {
      String selector = "#player" + i;
      Label player = (Label) mainPane.lookup(selector);
      selector = "#godCard" + i;
      ImageView card = (ImageView) mainPane.lookup(selector);

      if (!sexes.isEmpty() && guiManager.getData().getOwner().equals(guiManager.getData().getPlayers()[i])) {
        board.setStyle("-worker-img: url('/img/workers/" + i +
                (sexes.get(0).equals(Sex.Male) ? "M" : "F") + ".png');");
      }

      if (guiManager.getData().getCurrentPlayer().equals(guiManager.getData().getPlayers()[i])) {
        player.getStyleClass().add("current");
        card.getStyleClass().add("current");
      } else {
        player.getStyleClass().removeAll("current");
        card.getStyleClass().removeAll("current");
      }
    }

    if (disabled) {
      instruction.setText("Wait for " + guiManager.getData().getCurrentPlayer().getNickname() + "\n to place his workers...");
    } else if (!sexes.isEmpty()) {
      instruction.setText("Place your " + sexes.get(0).toString() + " worker.");
      guiManager.getClient().raise(new WorkerSelectionEvent(guiManager.getData().getOwner(), sexes.get(0)));
    }
  }

  @Override
  public void handle(BoardUpdateEvent e) {
    Board b = guiManager.getData().getBoard();

    for (int i = 0; i < b.getSize(); i++) {
      for (int j = 0; j < b.getSize(); j++) {
        Point p = new Point(i, j);
        if (b.getBox(p).getItems().peek() != null) {
          String selector = "#button" + i + j;
          ImageView cell = (ImageView) board.lookup(selector);

          for (int k = 0; k < guiManager.getNumberOfPlayers(); k++) {
            if (guiManager.getData().getPlayers()[k].getWorkers().values().stream().anyMatch(w -> w.equals(b.getBox(p).getItems().peek()))) {
              Worker w = (Worker) b.getBox(p).getItems().peek();
              Sex sex = w.getSex();
              cell.getStyleClass().remove("freeBox");
              cell.getStyleClass().add("box");
              cell.setImage(Settings.workersImages[k].get(sex));
            }
          }
        }
      }
    }
    updateView();
  }

  @Override
  public void handle(PlaceYourWorkersEvent e) {
    super.handle(e);
    updateView();
  }
}
