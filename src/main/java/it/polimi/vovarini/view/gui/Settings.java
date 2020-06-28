package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodName;
import javafx.scene.image.Image;

import java.util.HashMap;

public class Settings {

  public static final HashMap<GodName, Image> godImages = new HashMap<>();
  public static final HashMap<Sex, Image>[] workersImages = new HashMap[3];
  public static final HashMap<GodName, String> descriptions = new HashMap<>();
  public static final Image[] levelsImages = new Image[4];
  public static final Image bg = new Image(Settings.class.getClassLoader().getResource("img/bg.png").toExternalForm(), true);
  public static final Image victory = new Image(Settings.class.getClassLoader().getResource("img/victory.png").toExternalForm(), true);
  public static final Image loss = new Image(Settings.class.getClassLoader().getResource("img/loss.png").toExternalForm(), true);

  public static final String powers[] = {
          "Apollo: Quando ti muovi, puoi scambiarti con un lavoratore adiacente.",
          "Artemis: Puoi spostarti una volta in più, ma non puoi tornare alla casella da dove sei partito.\nPremi SKIP se non vuoi utilizzare il potere.",
          "Athena: Se nel tuo ultimo turno sei salito di livello, i tuoi avversari non potranno salire di livello.",
          "Atlas: Puoi costruire una cupola sopra qualsiasi blocco (premendo tasto destro).",
          "Demeter: Puoi costruire una volta in più, ma non nella stessa casella.\nPremi SKIP se non vuoi utilizzare il potere.",
          "Hephaestus: Puoi costruire un blocco aggiuntivo (non una cupola) al di sopra della casella su cui hai già costruito.\nPremi SKIP se non vuoi utilizzare il potere.",
          "Hera: Impedisci all'avversario di vincere se sale al terzo livello su una casella perimetrale.",
          "Hestia: Puoi costruire una volta in più in qualsiasi casella, a patto che non sia perimetrale.\nPremi SKIP se non vuoi utilizzare il potere.",
          "Limus: I lavoratori avversari non possono costruire nelle caselle adiacenti ai tuoi lavoratori, a meno che costruiscano una cupola per formare una torre completa.",
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

    int k = 0;
    for(GodName godName : GodName.values()){
      descriptions.put(godName, powers[k]);
      k++;
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
