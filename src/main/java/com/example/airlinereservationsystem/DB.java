package com.example.airlinereservationsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DB {

    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311airlinereserv.mysql.database.azure.com";//replace with server URL
    final String DB_URL = MYSQL_SERVER_URL + "/airline_reservation"; // Updated database name
    final String USERNAME = "Group5"; //replace with username
    final String PASSWORD = "AdminLog5"; //replace with password

    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;

        try {
            // First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS airline_reservation");
            statement.close();
            conn.close();

            // Second, connect to the database and create the table "users" if not created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(200) NOT NULL,"
                    + "email VARCHAR(200) NOT NULL UNIQUE,"
                    + "phone VARCHAR(200),"
                    + "address VARCHAR(200),"
                    + "password VARCHAR(200) NOT NULL"
                    + ")";
            statement.executeUpdate(sql);

            // Check if we have users in the table users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }

            // Create flight bookings table if not exists
            sql = "CREATE TABLE IF NOT EXISTS bookings ("
                    + "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id INT(10) NOT NULL,"
                    + "flight_number VARCHAR(50) NOT NULL,"
                    + "origin VARCHAR(50) NOT NULL,"
                    + "destination VARCHAR(50) NOT NULL,"
                    + "departure_date VARCHAR(50) NOT NULL,"
                    + "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id)"
                    + ")";
            statement.executeUpdate(sql);

            statement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

    /**
     * Verify user login credentials
     * @param username The username to check
     * @param password The password to verify
     * @return true if login successful, false otherwise
     */
    public boolean verifyLogin(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            boolean hasUser = resultSet.next(); // Will be true if user found

            preparedStatement.close();
            conn.close();

            return hasUser;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get user ID by username
     * @param username The username to look up
     * @return The user's ID or -1 if not found
     */
    public int getUserId(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT id FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            int userId = -1;

            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }

            preparedStatement.close();
            conn.close();

            return userId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Record a flight booking in the database
     * @param username The username booking the flight
     * @param flightNumber The flight number
     * @param origin Origin airport code
     * @param destination Destination airport code
     * @param departureDate Date of departure
     * @return true if booking was recorded successfully
     */
    public boolean recordBooking(String username, String flightNumber, String origin,
                                 String destination, String departureDate) {
        try {
            int userId = getUserId(username);
            if (userId == -1) return false;

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO bookings (user_id, flight_number, origin, destination, departure_date) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, flightNumber);
            preparedStatement.setString(3, origin);
            preparedStatement.setString(4, destination);
            preparedStatement.setString(5, departureDate);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the list of bookings for a specific user
     * @param username The username to get bookings for
     * @return List of booking details as strings
     */
    public List<String> getUserBookings(String username) {
        List<String> bookings = new ArrayList<>();

        try {
            int userId = getUserId(username);
            if (userId == -1) return bookings;

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String flightNumber = resultSet.getString("flight_number");
                String origin = resultSet.getString("origin");
                String destination = resultSet.getString("destination");
                String departureDate = resultSet.getString("departure_date");
                String bookingDate = resultSet.getString("booking_date");

                String bookingDetails = String.format(
                        "Flight: %s - From: %s to %s on %s (Booked on: %s)",
                        flightNumber, origin, destination, departureDate, bookingDate
                );

                bookings.add(bookingDetails);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public void queryUserByName(String name) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listAllUsers() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String name, String email, String phone, String address, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }
}