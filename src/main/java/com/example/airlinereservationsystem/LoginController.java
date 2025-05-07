package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType; // Added this import
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    private DB database = new DB(); // Create database instance

    @FXML
    private void initialize() {
        // Initialize database connection when the controller loads
        database.connectToDatabase();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String password = passwordField.getText();

        if (user.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
        } else if (user.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            // Admin login successful - Show confirmation dialog with options
            messageLabel.setText("Admin login successful!");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Admin Login");
            alert.setHeaderText("Welcome Admin!");
            alert.setContentText("Would you like to go to the Admin Navigation Menu or directly to Flight Booking?");

            ButtonType adminNavButton = new ButtonType("Admin Navigation");
            ButtonType flightBookingButton = new ButtonType("Flight Booking");

            alert.getButtonTypes().setAll(adminNavButton, flightBookingButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == adminNavButton) {
                    // Admin chose to go to the navigation menu
                    loadAdminNavigationScreen(event);
                } else {
                    // Admin chose to go directly to flight booking
                    loadFlightBookingScreen(event, "Admin Dashboard - Flight Booking", user);
                }
            });
        } else {
            // Check if user exists in database
            boolean loginSuccess = database.verifyLogin(user, password);

            if (loginSuccess) {
                messageLabel.setText("Logged in as " + user);
                loadFlightBookingScreen(event, "Flight Booking", user);
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        }
    }

    private void loadAdminNavigationScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/AdminNavigationScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Admin Navigation");
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading admin navigation screen: " + e.getMessage());
        }
    }

    private void loadFlightBookingScreen(ActionEvent event, String title, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/FlightBookingScreen.fxml"));
            Parent root = loader.load();

            // Get controller and pass the username
            FlightBookingController controller = loader.getController();
            controller.initializeUser(username);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading next screen: " + e.getMessage());
        }
    }

    @FXML
    private void switchToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/SignupScreen.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error switching to signup: " + e.getMessage());
        }
    }
}