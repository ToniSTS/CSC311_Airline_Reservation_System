# Airline Reservation System

A full-featured airline reservation system developed in Java using JavaFX and connected to a cloud-based Azure SQL database. This project simulates the real-world process of searching, booking, and managing flights with user-friendly GUI functionality and admin privileges.

## Project Summary

This application allows users to:
- Search for available flights based on origin, destination, and date
- View flight recommendations (including AI-powered suggestions via ChatGPT API)
- Book and manage flight reservations
- Admins can bypass payment and access all features
- Users can sign in, sign out, and view booking history

The goal of this project is to demonstrate proficiency in JavaFX, backend integration, cloud database interaction, and user-centered UI design, suitable for a capstone or portfolio project.

## Features

- JavaFX-based graphical user interface
- Secure login and session management
- Flight search with filters
- AI-powered flight recommendation system
- Booking confirmation and simulated payment flow
- Admin bypass functionality for streamlined testing
- Database-backed persistence using Azure SQL
- CSS styling for professional UI design

## Technologies Used

| Category             | Tools/Technologies                                   |
|----------------------|------------------------------------------------------|
| Language             | Java                                                 |
| GUI Framework        | JavaFX (FXML, SceneBuilder, CSS)                     |
| Backend              | Azure SQL Database                         |
| AI Integration       | OpenAI's ChatGPT API (via static method call)        |
| IDE                  | IntelliJ IDEA                                        |
| Version Control      | Git + GitHub                                         |
| Build Tool           | Maven                              |
| Testing              | Manual UI/UX testing, Exception handling logs        |

## Folder Structure

src/
└── main/
├── java/com/example/airlinereservationsystem/
│   ├── FlightBookingController.java
│   ├── PaymentController.java
│   ├── DB.java
│   └── … (Other backend logic)
└── resources/com/example/airlinereservationsystem/
├── FlightBookingScreen.fxml
├── PaymentScreen.fxml
├── PaymentConfirmationScreen.fxml
└── flightbooking.css

## Future Enhancements

- Full user authentication with hashed passwords
- Profile image upload and edit functionality
- PDF export of booking tickets
- Integration with real flight APIs (e.g., Skyscanner)

## Authors

**Jaydan Florian, Mauricio Rincon, Patrick Lomangino, Jeantonio Stsurin, Howard Gu 
