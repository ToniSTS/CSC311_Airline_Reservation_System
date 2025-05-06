module com.example.airlinereservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.airlinereservationsystem to javafx.fxml;
    exports com.example.airlinereservationsystem;
}