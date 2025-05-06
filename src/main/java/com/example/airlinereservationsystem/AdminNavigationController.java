package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Admin Navigation Screen
 */
public class AdminNavigationController {

    @FXML private Label welcomeLabel;
    @FXML private Button flightBookingButton;
    @FXML private Button userManagementButton;
    @FXML private Button paymentHistoryButton;
    @FXML private Button signOutButton;

    private final String ADMIN_USERNAME = "admin";

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, Admin! Select a screen to navigate to:");
    }

    @FXML
    private void handleFlightBooking(ActionEvent event) {
        navigateToScreen("/com/example/airlinereservationsystem/FlightBookingScreen.fxml",
                "Admin Dashboard - Flight Booking", flightBookingButton);
    }

    @FXML
    private void handleUserManagement(ActionEvent event) {
        // This would typically go to a user management screen
        // For now, we'll just show a simulated screen
        navigateToScreen("/com/example/airlinereservationsystem/FlightBookingScreen.fxml",
                "Admin Dashboard - User Management", userManagementButton);
    }

    @FXML
    private void handlePaymentHistory(ActionEvent event) {
        // Navigate directly to payment confirmation screen with sample data
        try {
            // Create a sample flight for demonstration purposes
            Flight sampleFlight = new Flight(
                    "AA123", "JFK", "LAX", "2025-05-06",
                    "08:00", "10:30", 299.99);

            // Load the payment confirmation screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/airlinereservationsystem/PaymentConfirmationScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and set up the sample data
            PaymentConfirmationController controller = loader.getController();
            controller.initData(sampleFlight, ADMIN_USERNAME, sampleFlight.getPrice());
            controller.setAdminMode(true); // Explicitly set admin mode

            // Set the scene
            Stage stage = (Stage) paymentHistoryButton.getScene().getWindow();
            stage.setTitle("Admin Dashboard - Payment History");
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading payment history: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/LoginScreen.fxml"));
            Stage stage = (Stage) signOutButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 400, 300));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error returning to login screen: " + e.getMessage());
        }
    }

    private void navigateToScreen(String fxmlPath, String title, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // If it's the flight booking screen, initialize the user as admin
            if (fxmlPath.contains("FlightBookingScreen")) {
                FlightBookingController controller = loader.getController();
                controller.initializeUser(ADMIN_USERNAME);
            }

            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error navigating to screen: " + e.getMessage());
        }
    }
}