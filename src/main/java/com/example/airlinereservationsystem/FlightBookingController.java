package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class FlightBookingController {

    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private ListView<Flight> resultsListView;
    @FXML private Label recommendationLabel;
    @FXML private Label statusLabel;

    private FlightRecommendationSystem recommendationSystem = new FlightRecommendationSystem();
    private FlightAPIService apiService = new FlightAPIService();
    private String currentUser = "guest"; // Default user

    @FXML
    private void initialize() {
        // Set today's date as default
        datePicker.setValue(LocalDate.now());

        // Configure the list view
        resultsListView.setCellFactory(param -> new ListCell<Flight>() {
            @Override
            protected void updateItem(Flight flight, boolean empty) {
                super.updateItem(flight, empty);
                if (empty || flight == null) {
                    setText(null);
                } else {
                    setText(flight.toString());
                }
            }
        });

        statusLabel.setText("Ready to search for flights");
    }

    public void initializeUser(String username) {
        this.currentUser = username;
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String origin = originField.getText().toUpperCase();
        String destination = destinationField.getText().toUpperCase();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";

        if (origin.isEmpty() || destination.isEmpty()) {
            showAlert("Please enter origin and destination airports");
            return;
        }

        statusLabel.setText("Searching for flights...");

        // Run the API call in a separate thread to keep UI responsive
        new Thread(() -> {
            try {
                // Search for flights using the real API
                List<Flight> flights = apiService.searchFlights(origin, destination, date);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    resultsListView.setItems(FXCollections.observableArrayList(flights));

                    // Show recommendations
                    List<String> recommendations = recommendationSystem.recommendFlights(currentUser, 2);

                    if (!recommendations.isEmpty()) {
                        recommendationLabel.setText("AI Recommendation: Consider these flights: " + String.join(", ", recommendations));
                    } else {
                        recommendationLabel.setText("No AI recommendations available yet");
                    }

                    statusLabel.setText("Found " + flights.size() + " flights");
                });
            } catch (Exception e) {
                e.printStackTrace();

                // Update UI on error
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleBookFlight(ActionEvent event) {
        Flight selectedFlight = resultsListView.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Please select a flight to book");
            return;
        }

        statusLabel.setText("Booking flight...");

        // Run booking in a separate thread
        new Thread(() -> {
            // Book the flight
            boolean success = apiService.bookFlight(selectedFlight.getFlightNumber(), currentUser, currentUser + "@example.com");

            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                if (success) {
                    showAlert("Flight " + selectedFlight.getFlightNumber() + " booked successfully!");

                    // Add a rating for this flight to improve future recommendations
                    recommendationSystem.addRating(currentUser, selectedFlight.getFlightNumber(), 5);
                    statusLabel.setText("Flight booked successfully");
                } else {
                    showAlert("Failed to book flight. Please try again.");
                    statusLabel.setText("Booking failed");
                }
            });
        }).start();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}