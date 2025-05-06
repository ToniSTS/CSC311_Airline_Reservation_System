package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField; // Added for email
    @FXML private TextField phoneField; // Added for phone
    @FXML private TextField addressField; // Added for address
    @FXML private Label messageLabel;

    private DB database = new DB(); // Create database instance

    @FXML
    private void initialize() {
        // Initialize database connection when the controller loads
        boolean hasUsers = database.connectToDatabase();
        if (hasUsers) {
            System.out.println("Database connected and has registered users");
        } else {
            System.out.println("Database connected, no users registered yet");
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String email = emailField != null ? emailField.getText() : user + "@example.com"; // Use field if available
        String phone = phoneField != null ? phoneField.getText() : ""; // Use field if available
        String address = addressField != null ? addressField.getText() : ""; // Use field if available

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("All fields are required.");
        } else if (!pass.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
        } else {
            try {
                // Insert the user into the database
                database.insertUser(user, email, phone, address, pass);

                messageLabel.setText("Account created for " + user + "!");

                // Wait 2 seconds and then switch to login screen
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            switchToLogin(event);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                messageLabel.setText("Error creating account: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            // Fix the path to point to the correct location
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/LoginScreen.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error switching to login: " + e.getMessage());
        }
    }
}