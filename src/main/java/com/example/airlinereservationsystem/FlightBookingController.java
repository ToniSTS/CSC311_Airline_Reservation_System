package com.example.airlinereservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    @FXML private Button viewBookingsButton;
    @FXML private Button chatGptButton;
    @FXML private Button signOutButton;
    @FXML private Button adminBypassButton; // Button visible only to admin users
    @FXML private Button backToAdminButton; // Back button for admin to return to admin menu

    private FlightRecommendationSystem recommendationSystem = new FlightRecommendationSystem();
    private FlightAPIService apiService = new FlightAPIService();
    private DB database = new DB();
    private String currentUser = "guest"; // Default user

    // Admin credentials
    private final String ADMIN_USERNAME = "admin";

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

        // Hide admin buttons by default
        if (adminBypassButton != null) {
            adminBypassButton.setVisible(false);
        }

        if (backToAdminButton != null) {
            backToAdminButton.setVisible(false);
        }

        // Initialize database connection
        database.connectToDatabase();
    }

    public void initializeUser(String username) {
        this.currentUser = username;
        statusLabel.setText("Welcome, " + username + "! Ready to search for flights.");

        // Show admin buttons only for admin user
        if (username.equals(ADMIN_USERNAME)) {
            if (adminBypassButton != null) {
                adminBypassButton.setVisible(true);
            }

            if (backToAdminButton != null) {
                backToAdminButton.setVisible(true);
            }

            statusLabel.setText("Welcome Admin! You have full access to all features.");
        }
    }

    @FXML
    private void handleBackToAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/AdminNavigationScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backToAdminButton.getScene().getWindow();
            stage.setTitle("Admin Navigation");
            stage.setScene(new Scene(root, 500, 500));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error returning to admin menu: " + e.getMessage());
        }
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

        // If admin user, bypass payment directly
        if (currentUser.equals(ADMIN_USERNAME)) {
            // Admin bypass - directly go to confirmation screen without database
            skipToConfirmation(selectedFlight);
        } else {
            // Regular user - proceed to payment screen
            statusLabel.setText("Proceeding to payment...");
            goToPaymentScreen(selectedFlight);
        }
    }

    @FXML
    private void handleAdminBypass(ActionEvent event) {
        // This method is called when the admin bypass button is clicked
        Flight selectedFlight = resultsListView.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Please select a flight to book with admin bypass");
            return;
        }

        // Admin bypass - directly go to confirmation screen without database
        skipToConfirmation(selectedFlight);
    }

    /**
     * For admin users - skip database validation and go straight to confirmation
     */
    private void skipToConfirmation(Flight selectedFlight) {
        statusLabel.setText("Admin bypass activated...");

        try {
            // Attempt to record in database but don't let it block us if it fails
            try {
                database.recordBooking(
                        currentUser,
                        selectedFlight.getFlightNumber(),
                        selectedFlight.getDepartureAirport(),
                        selectedFlight.getArrivalAirport(),
                        selectedFlight.getDepartureDate()
                );
            } catch (Exception e) {
                // Just log it but continue - we're in admin mode
                System.out.println("Warning: Could not record booking in database: " + e.getMessage());
            }

            // Load the confirmation screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/PaymentConfirmationScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the data
            PaymentConfirmationController controller = loader.getController();
            controller.initData(selectedFlight, currentUser, selectedFlight.getPrice());

            // Set the scene
            Stage stage = (Stage) resultsListView.getScene().getWindow();
            stage.setTitle("Booking Confirmation (Admin Mode)");
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading confirmation screen: " + e.getMessage());
            showAlert("Error: Could not load confirmation screen. " + e.getMessage());
        }
    }

    private void goToPaymentScreen(Flight selectedFlight) {
        try {
            // Load the payment screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinereservationsystem/PaymentScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the flight and username data
            PaymentController controller = loader.getController();
            controller.initData(selectedFlight, currentUser);

            // Set the scene
            Stage stage = (Stage) resultsListView.getScene().getWindow();
            stage.setTitle("Payment - Flight " + selectedFlight.getFlightNumber());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading payment screen: " + e.getMessage());
            showAlert("Error: Could not load payment screen. " + e.getMessage());
        }
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

    @FXML
    private void handleSignOut(ActionEvent event) {
        // Clear the current user session
        this.currentUser = "guest";

        // Show confirmation message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Out");
        alert.setHeaderText(null);
        alert.setContentText("You have been successfully signed out.");
        alert.showAndWait();

        // Return to the login screen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinereservationsystem/LoginScreen.fxml"));
            Stage stage = (Stage) signOutButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 400, 300));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error returning to login screen: " + e.getMessage());
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