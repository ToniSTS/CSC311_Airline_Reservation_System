package com.example.airlinereservationsystem;

public class Flight {
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private String departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;

    public Flight(String flightNumber, String departureAirport, String arrivalAirport,
                  String departureDate, String departureTime, String arrivalTime, double price) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    // Getters
    public String getFlightNumber() { return flightNumber; }
    public String getDepartureAirport() { return departureAirport; }
    public String getArrivalAirport() { return arrivalAirport; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return flightNumber + ": " + departureAirport + " to " + arrivalAirport +
                " on " + departureDate + " (" + departureTime + " - " + arrivalTime +
                ") - $" + price;
    }
}