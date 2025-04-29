package com.example.airlinereservationsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightRecommendationSystem {

    // Simple collaborative filtering recommendation system
    private Map<String, Map<String, Integer>> userFlightRatings = new HashMap<>();

    // Constructor to initialize with sample data
    public FlightRecommendationSystem() {
        // Add some sample ratings
        addRating("user1", "AA123", 5);
        addRating("user1", "DL456", 4);
        addRating("user2", "AA123", 4);
        addRating("user2", "UA789", 5);
        addRating("user3", "DL456", 5);
        addRating("user3", "UA789", 4);
        addRating("admin", "AA123", 3);
        addRating("admin", "B6234", 5);
    }

    // Add a user rating for a flight
    public void addRating(String userId, String flightId, int rating) {
        userFlightRatings.putIfAbsent(userId, new HashMap<>());
        userFlightRatings.get(userId).put(flightId, rating);
    }

    // Calculate similarity between two users (cosine similarity)
    private double calculateSimilarity(String user1, String user2) {
        Map<String, Integer> user1Ratings = userFlightRatings.get(user1);
        Map<String, Integer> user2Ratings = userFlightRatings.get(user2);

        // Check for null ratings to avoid NullPointerException
        if (user1Ratings == null || user2Ratings == null) {
            return 0.0;
        }

        // Find common flights rated by both users
        List<String> commonFlights = new ArrayList<>();
        for (String flight : user1Ratings.keySet()) {
            if (user2Ratings.containsKey(flight)) {
                commonFlights.add(flight);
            }
        }

        if (commonFlights.isEmpty()) {
            return 0.0; // No similarity if no common flights
        }

        // Calculate dot product
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String flight : commonFlights) {
            dotProduct += user1Ratings.get(flight) * user2Ratings.get(flight);
            norm1 += Math.pow(user1Ratings.get(flight), 2);
            norm2 += Math.pow(user2Ratings.get(flight), 2);
        }

        // Avoid division by zero
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Recommend flights for a user
    public List<String> recommendFlights(String userId, int numRecommendations) {
        List<String> recommendations = new ArrayList<>();

        // If user doesn't exist, return some default recommendations
        if (!userFlightRatings.containsKey(userId)) {
            // Return some default popular flights
            recommendations.add("AA123");
            recommendations.add("DL456");
            recommendations.add("UA789");
            return recommendations.subList(0, Math.min(numRecommendations, recommendations.size()));
        }

        Map<String, Double> flightScores = new HashMap<>();
        Map<String, Integer> userRatings = userFlightRatings.get(userId);

        // For each other user
        for (String otherUser : userFlightRatings.keySet()) {
            if (otherUser.equals(userId)) continue;

            double similarity = calculateSimilarity(userId, otherUser);

            // Skip users with no similarity
            if (similarity <= 0) continue;

            // For each flight rated by the other user
            for (Map.Entry<String, Integer> entry : userFlightRatings.get(otherUser).entrySet()) {
                String flightId = entry.getKey();
                int rating = entry.getValue();

                // Skip flights the current user has already rated
                if (userRatings.containsKey(flightId)) continue;

                // Add weighted rating to flight score
                flightScores.putIfAbsent(flightId, 0.0);
                flightScores.put(flightId, flightScores.get(flightId) + similarity * rating);
            }
        }

        // Sort flights by score and return top recommendations
        recommendations = flightScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .toList();

        // If we couldn't find enough recommendations, add some default ones
        if (recommendations.size() < numRecommendations) {
            List<String> defaults = List.of("AA123", "DL456", "UA789", "B6234", "WN567");
            for (String flight : defaults) {
                if (!recommendations.contains(flight) && !userRatings.containsKey(flight)) {
                    recommendations.add(flight);
                    if (recommendations.size() >= numRecommendations) break;
                }
            }
        }

        return recommendations;
    }
}