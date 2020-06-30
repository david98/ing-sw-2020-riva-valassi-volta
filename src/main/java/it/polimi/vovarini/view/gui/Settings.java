package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodName;
import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.HashMap;

public class Settings {

  public static final HashMap<GodName, Image> godImages = new HashMap<>();
  public static final HashMap<Sex, Image>[] workersImages = new HashMap[3];
  public static final Image[] levelsImages = new Image[4];
  public static final Image bg = new Image(Settings.class.getClassLoader().getResource("img/bg.png").toExternalForm(), true);
  public static final Image victory = new Image(Settings.class.getClassLoader().getResource("img/victory.png").toExternalForm(), true);
  public static final Image loss = new Image(Settings.class.getClassLoader().getResource("img/loss.png").toExternalForm(), true);

  public static final HashMap<GodName, String> descriptions = new HashMap<>();

  public static final String powers[] = {
          "Apollo: When moving, you can exchange your worker with one of an opponent, if adjacent.",
          "Artemis: You can move once more, but you cannot get back to the box you just left.\nPress SKIP if you do not wish to use the power.",
          "Athena: If in your last turn you moved up a level, your opponents won't be able to until their next turn.",
          "Atlas: You can build a dome on top of any block (with a right click). With a left click, you build following the standard rules.",
          "Demeter: You can build once more, but you cannot build on the same box you just built upon. \nPress SKIP if you do not wish to use the power.",
          "Hephaestus: You can build an additional block (not a dome) on the same box.\nPress SKIP if you do not wish to use the power.",
          "Hera: Your opponents cannot win if they move from level 2 to level 3 while in a box in the perimeter.",
          "Hestia: You can build once more on every box, excluding the ones on the perimeter.\nPress SKIP if you do not wish to use the power.",
          "Limus: Your opponent's workers cannot build in boxes adjacent to yours, unless if they build a dome to complete a tower.",
          "Minotaur: Puoi forzare una pedina avversaria adiacente a spostarsi in una direzione libera, prendendo il suo posto. L'avversario è obbligato a spostarsi, indipendentemente dal livello.",
          "Pan: Puoi vincere anche scendendo di due o più livelli.",
          "Prometheus: Puoi costruire sia prima che dopo esserti mosso, a patto che tu non salga di livello.\nPremi SKIP se non vuoi utilizzare il potere (effettuerai il normale turno di gioco).",
          "Triton: Ogni volta che il tuo lavoratore si trova su una casella perimetrale, può spostarsi di nuovo indipendentemente dal livello.\nPremi SKIP se vuoi smettere di utilizzare il potere.",
          "Zeus: Il tuo lavoratore può costruire nella casella al di sotto di sé, aumentandone il livello.\nPremi SKIP se non vuoi utilizzare il potere.",
          "Nobody: I tuoi lavoratori sono noiosi, punto."

  };

  static {
    for (GodName name: GodName.values()) {
      if (name != GodName.Nobody) {
        godImages.put(name, new Image(Settings.class.getClassLoader().getResource("img/godCards/" + name.toString() + ".png")
                .toExternalForm(), true));
      }
    }

    for (int i = 0; i < 3; i++) {
      workersImages[i] = new HashMap<>();
      workersImages[i].put(Sex.Male, new Image(Settings.class.getClassLoader().getResource("img/workers/" + i + "M.png").toExternalForm(), true));
      workersImages[i].put(Sex.Female, new Image(Settings.class.getClassLoader().getResource("img/workers/" + i + "F.png").toExternalForm(), true));
    }

    for (int i = 0; i < 4; i++) {
      levelsImages[i] = new Image(Settings.class.getClassLoader().getResource(("img/levels/" + (i + 1) + ".png")).toExternalForm(), true);
    }

    GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);

    for(int i = 0; i < godNames.length; i++) {
        descriptions.put(godNames[i], powers[i]);
    }

  }

  public static final String ELECTED_PLAYER_SCENE_FXML = "/fxml/electedPlayerScene.fxml";
  public static final String GOD_CARD_SELECTION_SCENE_FXML = "/fxml/godCardSelectionScene.fxml";
  public static final String REGISTRATION_SCENE_FXML = "/fxml/registrationScene.fxml";
  public static final String CONNECTION_SCENE_FXML = "/fxml/connectionScene.fxml";
  public static final String SPAWN_WORKER_SCENE_FXML = "/fxml/spawnWorkerScene.fxml";
  public static final String WAIT_SCENE_FXML = "/fxml/waitScene.fxml";
  public static final String GAME_SCENE_FXML = "/fxml/gameScene.fxml";

  public static final String DUPLICATE_NICKNAME = "Nickname already exists, type a new one";
  public static final String INVALID_NICKNAME = "Invalid nickname, type a new one";

  public static void load() {
    // needed to load images
    for (var img: godImages.values()) {
      img.toString();
    }

    for (var img: workersImages) {
      img.get(Sex.Male).toString();
      img.get(Sex.Female).toString();
    }

    for (var lvlImg: levelsImages) {
      lvlImg.toString();
    }

    bg.toString();
    victory.toString();
    loss.toString();
  }

}
