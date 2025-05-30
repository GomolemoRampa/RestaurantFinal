package com.cs.restaurantfinal;

public class RestaurantRating {
    private String restaurantName;
    private int totalOrders;
    private int positiveRatings;
    private int negativeRatings;
    private double averageRating;

    public RestaurantRating() {}

    public RestaurantRating(String restaurantName) {
        this.restaurantName = restaurantName;
        this.totalOrders = 0;
        this.positiveRatings = 0;
        this.negativeRatings = 0;
        this.averageRating = 0.0;
    }

    public void updateRating(Order.Rating rating) {
        if (rating == Order.Rating.THUMBS_UP) {
            positiveRatings++;
        } else if (rating == Order.Rating.THUMBS_DOWN) {
            negativeRatings++;
        }
        calculateAverageRating();
    }

    private void calculateAverageRating() {
        int totalRated = positiveRatings + negativeRatings;
        if (totalRated > 0) {
            averageRating = (double) positiveRatings / totalRated * 5.0;
        } else {
            averageRating = 0.0;
        }
    }

    // Getters and Setters
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getPositiveRatings() { return positiveRatings; }
    public void setPositiveRatings(int positiveRatings) { this.positiveRatings = positiveRatings; }

    public int getNegativeRatings() { return negativeRatings; }
    public void setNegativeRatings(int negativeRatings) { this.negativeRatings = negativeRatings; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}