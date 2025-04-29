module com.example.airlinereservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.airlinereservationsystem to javafx.fxml;
    exports com.example.airlinereservationsystem;
}