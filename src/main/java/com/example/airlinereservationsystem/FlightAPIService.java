package com.example.airlinereservationsystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class FlightAPIService {

    private static final String API_KEY = "4b849960d8ecc1ff2dc388f1896bb61f"; // Your Aviationstack API key
    private static final String BASE_URL = "https://api.aviationstack.com/v1"; // Using HTTPS for secure connection

    /**
     * Search for flights with the given parameters
     */
    public List<Flight> searchFlights(String origin, String destination, String date) {
        // Validate that origin and destination are different
        if (origin.equals(destination)) {
            System.out.println("Error: Origin and destination airports cannot be the same");
            return new ArrayList<>(); // Return empty list
        }

        List<Flight> flights = new ArrayList<>();

        try {
            // Build the API URL for flight schedules
            String apiUrl = BASE_URL + "/flights"
                    + "?access_key=" + API_KEY
                    + "&dep_iata=" + origin
                    + "&arr_iata=" + destination;

            // Add date parameter if provided
            if (date != null && !date.isEmpty()) {
                apiUrl += "&flight_date=" + date;
            }

            System.out.println("Requesting: " + apiUrl);

            // Make the API request
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                System.out.println("Error response: " + conn.getResponseMessage());
                // Return fallback flights if API is not working
                return getFallbackFlights(origin, destination, date);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Check if there's an error
            if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                System.out.println("API Error Details: " + error.toString());
                // Print the full response for debugging
                System.out.println("Full API Response: " + response.toString().substring(0, Math.min(500, response.toString().length())));
                return getFallbackFlights(origin, destination, date);
            }

            // Extract the data array
            JSONArray flightsArray = jsonResponse.getJSONArray("data");
            System.out.println("Found " + flightsArray.length() + " flights");

            // Convert JSON to Flight objects
            for (int i = 0; i < flightsArray.length(); i++) {
                try {
                    JSONObject flightJson = flightsArray.getJSONObject(i);

                    JSONObject departure = flightJson.getJSONObject("departure");
                    JSONObject arrival = flightJson.getJSONObject("arrival");
                    JSONObject flight = flightJson.getJSONObject("flight");

                    String flightNumber = flight.getString("iata");
                    String departureAirport = departure.getString("iata");
                    String arrivalAirport = arrival.getString("iata");

                    // Parse date and time from scheduled time
                    String departureDateTime = departure.getString("scheduled");
                    String departureDate = departureDateTime.split("T")[0];
                    String departureTime = departureDateTime.split("T")[1].substring(0, 5);

                    String arrivalDateTime = arrival.getString("scheduled");
                    String arrivalTime = arrivalDateTime.split("T")[1].substring(0, 5);

                    // Generate a price (not provided by API)
                    double price = 200 + Math.random() * 400;

                    Flight flightObj = new Flight(flightNumber, departureAirport, arrivalAirport,
                            departureDate, departureTime, arrivalTime, price);
                    flights.add(flightObj);
                } catch (Exception e) {
                    System.out.println("Error parsing flight at index " + i + ": " + e.getMessage());
                    // Continue with next flight
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception during API call: " + e.getMessage());
            e.printStackTrace();

            // Return fallback flights if API is not working
            return getFallbackFlights(origin, destination, date);
        }

        // If no flights were found, use fallback
        if (flights.isEmpty()) {
            System.out.println("No flights found, using fallbacks");
            return getFallbackFlights(origin, destination, date);
        }

        return flights;
    }

    /**
     * Get fallback flights in case the API doesn't work
     */
    private List<Flight> getFallbackFlights(String origin, String destination, String date) {
        // Double-check that origin and destination are different, even in fallback
        if (origin.equals(destination)) {
            System.out.println("Cannot generate fallback flights: Origin and destination are the same");
            return new ArrayList<>();
        }

        List<Flight> flights = new ArrayList<>();

        // Add some sample flights
        flights.add(new Flight("AA123", origin, destination, date, "08:00", "10:30", 299.99));
        flights.add(new Flight("DL456", origin, destination, date, "10:15", "12:45", 349.99));
        flights.add(new Flight("UA789", origin, destination, date, "13:30", "16:00", 279.99));
        flights.add(new Flight("B6234", origin, destination, date, "16:45", "19:15", 319.99));
        flights.add(new Flight("WN567", origin, destination, date, "19:30", "22:00", 259.99));

        return flights;
    }

    /**
     * Book a flight with the given details
     * Note: This is a simulation since the free API doesn't support booking
     */
    public boolean bookFlight(String flightNumber, String passengerName, String email) {
        // In a real implementation, you would make an API call for booking
        // For now, we'll simulate a booking transaction
        try {
            System.out.println("Booking flight " + flightNumber + " for " + passengerName + " (" + email + ")");

            // Simulate API processing time
            Thread.sleep(1500);

            // Simulate 90% success rate
            boolean success = Math.random() < 0.9;

            if (success) {
                System.out.println("Booking successful!");
            } else {
                System.out.println("Booking failed!");
            }

            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}