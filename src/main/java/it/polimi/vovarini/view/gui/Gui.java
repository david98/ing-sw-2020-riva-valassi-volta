package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.model.godcards.GodName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Arrays;

public class Gui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/electedPlayerScene.fxml"));
        primaryStage.setTitle("Cards choice");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(600);
        primaryStage.show();

        GodName[] godNames = Arrays.stream(GodName.values()).filter(name -> name != GodName.Nobody).toArray(GodName[]::new);
        String selector;
        for(int i = 0; i < godNames.length; i++) {
            selector = "#button" + i;
            Button temp = (Button) primaryStage.getScene().lookup(selector);
            String url = "url('/img/" + godNames[i].name() + ".png');";
            temp.setStyle("-fx-background-size: cover; -fx-background-image: " + url);

          /*  Image cardImg = new Image("resources/img/" + godNames[i].name() + ".png");
            ImageView cardImgView = new ImageView(cardImg);
            cardImgView.fitHeightProperty().bind(primaryStage.heightProperty());
            cardImgView.fitWidthProperty().bind(primaryStage.widthProperty());
            temp.setGraphic(cardImgView);*/

            //temp.setText(godNames[i].name());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
