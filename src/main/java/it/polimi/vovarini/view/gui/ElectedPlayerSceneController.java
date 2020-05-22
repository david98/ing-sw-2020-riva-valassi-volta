package it.polimi.vovarini.view.gui;

import it.polimi.vovarini.model.godcards.GodName;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.LinkedList;
import java.util.List;

public class ElectedPlayerSceneController {
    @FXML
    private Button godCard1;

    @FXML
    private Button godCard2;

    @FXML
    private Button godCard3;

    @FXML
    private Button button0;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button button5;

    @FXML
    private Button button6;

    @FXML
    private Button button7;

    @FXML
    private Button button8;

    @FXML
    private Button button9;

    @FXML
    private Button button10;

    @FXML
    private Button button11;

    @FXML
    private Button button12;

    @FXML
    private Button button13;

    private List<GodName> selectedCards = new LinkedList<>();

    @FXML
    void selectCard(MouseEvent event) {

        //String url = "url('resources/img/Apollo.png');";
        //button0.setStyle("-fx-background-image: " + url + "");

        /*Image apollo = new Image("resources/img/Apollo.png");
        System.out.println(apollo.getUrl());
        ImageView a = new ImageView(apollo);
        a.setFitHeight(50);
        a.setFitWidth(100);
        button0.setGraphic(a);*/

       godCard1.setStyle(((Button) event.getSource()).getStyle());

        Button source = (Button) event.getSource();
        System.out.println(source.getStyle());
/*

        if(selectedCards.contains(GodName.valueOf(source.getText()))) {
            source.setStyle("");
            selectedCards.remove(GodName.valueOf(source.getText()));
        } else if(selectedCards.size() < 3) {
            source.setStyle("-fx-background-color: yellow");
            selectedCards.add(GodName.valueOf(source.getText()));
        }

        String label = selectedCards.toString();
        selectedGodCards.setText(label);
*/
    }
}
