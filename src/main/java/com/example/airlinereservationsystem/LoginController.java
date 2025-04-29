package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String password = passwordField.getText();

        if (user.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
        } else if (user.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            // Admin login successful
            messageLabel.setText("Admin login successful!");

            try {
                // Load the flight booking screen
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/FlightBookingScreen.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setTitle("Admin Dashboard - Flight Booking");
                stage.setScene(new Scene(root, 600, 400));
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Error loading next screen: " + e.getMessage());
            }
        } else {
            // For demo purposes, any username/password combination works
            messageLabel.setText("Logged in as " + user);

            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/FlightBookingScreen.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setTitle("Flight Booking");
                stage.setScene(new Scene(root, 600, 400));
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Error loading next screen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void switchToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/SignupScreen.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 350));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error switching to signup: " + e.getMessage());
        }
    }
}