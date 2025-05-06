package com.example.airlinereservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    @FXML private Button payButton;

    // Called when "Pay Now" button is clicked
    @FXML
    public void handlePayNow(ActionEvent event) {
        String name = cardName.getText().trim();
        String number = cardNumber.getText().trim();
        String expiry = cardExpiry.getText().trim();
        String cvv = cardCVV.getText().trim();

        if (!isValidCardInfo(name, number, expiry, cvv)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid card details.");
            return;
        }

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing();

        if (paymentSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Payment Success", "Your payment was successful!");
            goToMainMenu();
        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Failed", "There was a problem processing your payment.");
        }
    }

    private boolean isValidCardInfo(String name, String number, String expiry, String cvv) {
        return !name.isEmpty()
                && number.matches("\\d{16}")
                && expiry.matches("(0[1-9]|1[0-2])/\\d{2}")
                && cvv.matches("\\d{3}");
    }

    private boolean simulatePaymentProcessing() {
        // For demo purposes, always return true.
        // You could randomly return false to simulate a failed payment.
        return true;
    }

    private void goToMainMenu() {
        try {
            Parent mainMenu = FXMLLoader.load(getClass().getResource("MainMenuView.fxml"));
            Stage stage = (Stage) payButton.getScene().getWindow();
            stage.setScene(new Scene(mainMenu));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load Main Menu.");
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
