package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightBookingController {

    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private ListView<Flight> resultsListView;
    @FXML private Label recommendationLabel;
    @FXML private Label statusLabel;
    @FXML private Button viewBookingsButton; // Add this if you have a button in FXML

    @FXML private Button chatGptButton;


    private FlightRecommendationSystem recommendationSystem = new FlightRecommendationSystem();
    private FlightAPIService apiService = new FlightAPIService();
    private DB database = new DB(); // Add database connection
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

        // Initialize database connection
        database.connectToDatabase();
    }

    public void initializeUser(String username) {
        this.currentUser = username;
        statusLabel.setText("Welcome, " + username + "! Ready to search for flights.");
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

        // Check if origin and destination are the same
        if (origin.equals(destination)) {
            showAlert("Origin and destination airports cannot be the same");
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

                    // Try to get a recommendation from ChatGPT if available
                    try {
                        if (!recommendations.isEmpty()) {
                            recommendationLabel.setText("AI Recommendation: Consider these flights: " + String.join(", ", recommendations));
                        } else {
                            // ChatGPT recommendation fallback
                            String aiRec = ChatGpt.getFlightRecommendation(origin, destination);
                            recommendationLabel.setText("AI Recommendation: Consider this flight: " + aiRec);
                        }
                    } catch (Exception e) {
                        // If ChatGPT fails, use the existing recommendation
                        if (!recommendations.isEmpty()) {
                            recommendationLabel.setText("AI Recommendation: Consider these flights: " + String.join(", ", recommendations));
                        } else {
                            recommendationLabel.setText("No AI recommendations available yet");
                        }
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
                    // Record the booking in the database
                    try {
                        boolean recordSuccess = database.recordBooking(
                                currentUser,
                                selectedFlight.getFlightNumber(),
                                selectedFlight.getDepartureAirport(),
                                selectedFlight.getArrivalAirport(),
                                selectedFlight.getDepartureDate()
                        );

                        if (recordSuccess) {
                            showAlert("Flight " + selectedFlight.getFlightNumber() + " booked successfully and recorded in your account!");
                        } else {
                            showAlert("Flight " + selectedFlight.getFlightNumber() + " booked successfully, but there was an issue recording it in your account.");
                        }
                    } catch (Exception e) {
                        showAlert("Flight " + selectedFlight.getFlightNumber() + " booked successfully, but there was an error recording it: " + e.getMessage());
                    }

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

    @FXML
    private void handleViewBookings(ActionEvent event) {
        statusLabel.setText("Loading your bookings...");

        // Run in a separate thread
        new Thread(() -> {
            try {
                // Get user's bookings from database
                List<String> bookings = database.getUserBookings(currentUser);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
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

                    statusLabel.setText("Ready to search for flights");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error loading bookings: " + e.getMessage());
                    statusLabel.setText("Error loading bookings");
                });
            }
        }).start();
    }

    @FXML
    private void handleChatGptRecommendation(ActionEvent event) {
        String origin = originField.getText().toUpperCase();
        String destination = destinationField.getText().toUpperCase();

        if (origin.isEmpty() || destination.isEmpty()) {
            showAlert("Please enter both origin and destination to get a recommendation.");
            return;
        }

        if (origin.equals(destination)) {
            showAlert("Origin and destination cannot be the same.");
            return;
        }

        statusLabel.setText("Getting ChatGPT recommendation...");

        new Thread(() -> {
            try {
                String recommendation = ChatGpt.getFlightRecommendation(origin, destination);
                javafx.application.Platform.runLater(() -> {
                    recommendationLabel.setText("ChatGPT Suggestion: " + recommendation);
                    statusLabel.setText("Recommendation received");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Failed to get recommendation from ChatGPT: " + e.getMessage());
                    statusLabel.setText("Recommendation failed");
                });
            }
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