package com.example.airlinereservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PaymentController {

    @FXML private TextField cardName;
    @FXML private TextField cardNumber;
    @FXML private TextField cardExpiry;
    @FXML private TextField cardCVV;
    @FXML private TextField billingAddress;
    @FXML private Button payButton;
    @FXML private Button cancelButton;
    @FXML private Label flightInfoLabel;
    @FXML private Label priceLabel;

    private Flight selectedFlight;
    private String username;
    private DB database = new DB();

    public void initData(Flight flight, String username) {
        this.selectedFlight = flight;
        this.username = username;

        // Update the flight information label
        flightInfoLabel.setText(String.format(
                "Flight: %s - From: %s to %s on %s (%s - %s)",
                flight.getFlightNumber(),
                flight.getDepartureAirport(),
                flight.getArrivalAirport(),
                flight.getDepartureDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime()
        ));

        // Update the price label
        priceLabel.setText(String.format("Total Price: $%.2f", flight.getPrice()));
    }

    @FXML
    public void initialize() {
        // Initialize database connection
        database.connectToDatabase();
    }

    // Called when "Pay Now" button is clicked
    @FXML
    public void handlePayNow(ActionEvent event) {
        // Validate payment information
        if (!validatePaymentInfo()) {
            return;
        }

        // Show processing payment message
        showAlert(Alert.AlertType.INFORMATION, "Processing", "Processing your payment...");

        // Simulate payment processing (in a real app, you would call a payment API)
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(1500);

                // Simulate a successful payment
                boolean paymentSuccess = Math.random() < 0.9; // 90% success rate

                javafx.application.Platform.runLater(() -> {
                    if (paymentSuccess) {
                        // Record the booking in the database
                        boolean bookingSuccess = database.recordBooking(
                                username,
                                selectedFlight.getFlightNumber(),
                                selectedFlight.getDepartureAirport(),
                                selectedFlight.getArrivalAirport(),
                                selectedFlight.getDepartureDate()
                        );

                        if (bookingSuccess) {
                            showAlert(Alert.AlertType.INFORMATION, "Success",
                                    "Payment successful! Your flight has been booked.");

                            // Return to flight booking screen
                            goToFlightBookingScreen();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Booking Error",
                                    "Payment was successful, but there was an error recording your booking. Please contact support.");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Payment Failed",
                                "There was an issue processing your payment. Please try again or use a different payment method.");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();

                javafx.application.Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "An unexpected error occurred during payment processing.");
                });
            }
        }).start();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Return to flight booking screen without processing payment
        goToFlightBookingScreen();
    }

    private boolean validatePaymentInfo() {
        String name = cardName.getText().trim();
        String number = cardNumber.getText().trim().replaceAll("\\s+", ""); // Remove spaces
        String expiry = cardExpiry.getText().trim();
        String cvv = cardCVV.getText().trim();
        String address = billingAddress.getText().trim();

        // Check if fields are empty
        if (name.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        // Validate card number format (16 digits)
        if (!number.matches("\\d{16}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Card number must be 16 digits.");
            return false;
        }

        // Validate expiry date format (MM/YY)
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Expiry date must be in MM/YY format.");
            return false;
        }

        // Validate CVV format (3 digits)
        if (!cvv.matches("\\d{3}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "CVV must be 3 digits.");
            return false;
        }

        return true;
    }

    private void goToFlightBookingScreen() {
        try {
            // Load the flight booking screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/FlightBookingScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the username
            FlightBookingController controller = loader.getController();
            controller.initializeUser(username);

            // Set the scene
            Stage stage = (Stage) payButton.getScene().getWindow();
            stage.setTitle("Flight Booking");
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Error returning to flight booking screen: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}