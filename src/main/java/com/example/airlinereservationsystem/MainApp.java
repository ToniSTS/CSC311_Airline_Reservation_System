package com.example.airlinereservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/WelcomeScreen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/airlinereservationsystem/WelcomeStyle.css")).toExternalForm());

        primaryStage.setTitle("Airline Reservation - Welcome!");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}