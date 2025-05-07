package com.example.airlinereservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PaymentConfirmationController {

    @FXML private Label bookingReferenceLabel;
    @FXML private Label flightDetailsLabel;
    @FXML private Label passengerLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Button returnButton;
    @FXML private Button viewBookingsButton;
    @FXML private Button backToAdminButton;

    private String username;
    private Flight bookedFlight;
    private String bookingReference;
    private DB database = new DB();
    private boolean isAdminMode = false;

    @FXML
    public void initialize() {
        // Initialize database connection
        try {
            database.connectToDatabase();
        } catch (Exception e) {
            System.out.println("Warning: Could not connect to database in confirmation screen: " + e.getMessage());
            // Continue anyway - we're in a confirmation screen
        }

        // Hide back to admin button by default
        if (backToAdminButton != null) {
            backToAdminButton.setVisible(false);
        }
    }

    /**
     * Explicitly set admin mode and update UI accordingly
     */
    public void setAdminMode(boolean isAdmin) {
        this.isAdminMode = isAdmin;

        // Show back to admin button for admin users
        if (backToAdminButton != null) {
            backToAdminButton.setVisible(isAdmin);
            System.out.println("Admin mode set to: " + isAdmin + " - Back button visible: " + backToAdminButton.isVisible());
        } else {
            System.out.println("Warning: backToAdminButton is null");
        }
    }

    public void initData(Flight flight, String username, double amount) {
        this.bookedFlight = flight;
        this.username = username;

        // Check if admin mode
        if ("admin".equals(username)) {
            isAdminMode = true;

            // Show back to admin button for admin users
            if (backToAdminButton != null) {
                backToAdminButton.setVisible(true);
                System.out.println("Admin user detected - Setting back button visible");
            } else {
                System.out.println("Warning: backToAdminButton is null in initData");
            }
        }

        // Generate a random booking reference
        this.bookingReference = generateBookingReference();

        // Update UI with booking details
        bookingReferenceLabel.setText("Booking Reference: " + bookingReference);

        flightDetailsLabel.setText(String.format(
                "Flight: %s - From: %s to %s\nDate: %s\nDeparture: %s - Arrival: %s",
                flight.getFlightNumber(),
                flight.getDepartureAirport(),
                flight.getArrivalAirport(),
                flight.getDepartureDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime()
        ));

        passengerLabel.setText("Passenger: " + username);
        totalAmountLabel.setText(String.format("Total Amount: $%.2f", amount));
    }

    private String generateBookingReference() {
        // Generate a random alphanumeric booking reference
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    @FXML
    public void handleBackToAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/AdminNavigationScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backToAdminButton.getScene().getWindow();
            stage.setTitle("Admin Navigation");
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error returning to admin menu: " + e.getMessage());
        }
    }

    @FXML
    public void handleViewBookings(ActionEvent event) {
        // Load user's bookings
        try {
            List<String> bookings;

            if (isAdminMode) {
                try {
                    bookings = database.getUserBookings(username);
                } catch (Exception e) {
                    // Create a fake booking entry for admin if database fails
                    bookings = new ArrayList<>();
                    bookings.add(String.format(
                            "Flight: %s - From: %s to %s on %s (Booked on: Today)",
                            bookedFlight.getFlightNumber(),
                            bookedFlight.getDepartureAirport(),
                            bookedFlight.getArrivalAirport(),
                            bookedFlight.getDepartureDate()
                    ));
                }
            } else {
                bookings = database.getUserBookings(username);
            }

            if (bookings.isEmpty()) {
                showAlert("You have no bookings yet.");
            } else {
                StringBuilder message = new StringBuilder("Your bookings:\n\n");
                for (String booking : bookings) {
                    message.append(booking).append("\n");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Your Bookings");
                alert.setHeaderText(null);
                alert.setContentText(message.toString());
                alert.getDialogPane().setPrefSize(500, 300);
                alert.showAndWait();
            }
        } catch (Exception e) {
            showAlert("Error loading bookings: " + e.getMessage());
        }
    }

    @FXML
    public void handleReturn(ActionEvent event) {
        try {
            // Load the flight booking screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/FlightBookingScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the username
            FlightBookingController controller = loader.getController();
            controller.initializeUser(username);

            // Set the scene
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setTitle("Flight Booking");
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error returning to flight booking screen: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}