package com.example.airlinereservationsystem;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Button getStartedBtn;

    @FXML
    private ImageView logoImage;

    @FXML
    public void handleGetStarted(ActionEvent event) throws IOException {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), ((Node) event.getSource()).getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fadeOut.play();
    }
}

