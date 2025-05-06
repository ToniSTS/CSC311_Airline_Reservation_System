package com.example.airlinereservationsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ChatGpt {

    public static void main(String[] args) {
        System.out.println(chatGPT("Hello, can you recommend a flight?"));
    }

    private static String chatGPT(String message) {
        // Simulated response to avoid API calls
        System.out.println("ChatGPT received prompt: " + message);

        // Generate a simple response based on the message
        if (message.toLowerCase().contains("flight")) {
            return "I recommend checking flights with major airlines like AA, DL, or UA for the best options.";
        } else {
            return "I'm here to help with flight recommendations!";
        }

        /* Original API code commented out
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "YOUR_API_KEY_HERE"; // API key
        String model = "gpt-4.1"; // current model in use for chatgpt api


        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");


            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();


            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
    }


    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        /* Original code commented out
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
        */

        // Since we're not making actual API calls, just return the response
        return response;
    }

    // Method to get flight recommendation - added to work with the rest of the code
    public static String getFlightRecommendation(String origin, String destination) {
        System.out.println("Generating flight recommendation from " + origin + " to " + destination);

        // Generate a flight number based on the origin and destination
        String airline = "";

        // Generate a random airline code based on the first letters of origin/destination
        if (origin.charAt(0) == 'A' || destination.charAt(0) == 'A') {
            airline = "AA"; // American Airlines
        } else if (origin.charAt(0) == 'D' || destination.charAt(0) == 'D') {
            airline = "DL"; // Delta
        } else if (origin.charAt(0) == 'U' || destination.charAt(0) == 'U') {
            airline = "UA"; // United
        } else {
            // Default airlines for other origins
            String[] airlines = {"AA", "DL", "UA", "B6", "WN"};
            airline = airlines[(int)(Math.random() * airlines.length)];
        }

        // Generate a random flight number
        int flightNum = 100 + (int)(Math.random() * 900);

        return airline + flightNum;
    }
}