package com.example.mobdeves19mcogr4;

import java.util.List;

public class Cafe {
    private String name;
    private String location;
    private String status;
    private String imageUrl;
    private boolean hasWifi;
    private boolean isDetails;
    private boolean isFoodPlace;
    private boolean isFavorite;
    private int image;
    private float rating;
    private List<String> reviews;
    private String placeId;
    private boolean servesFood;

    public Cafe(String name, String location, String status, String imageUrl, boolean hasWifi, boolean isDetails, boolean isFoodPlace, boolean isFavorite, int image, String placeId, boolean servesFood) {
        this.name = name;
        this.location = location;
        this.status = status;
        this.imageUrl = imageUrl;
        this.hasWifi = hasWifi;
        this.isDetails = isDetails;
        this.isFoodPlace = isFoodPlace;
        this.isFavorite = false;
        this.image = image;
        this.placeId = placeId;
        this.servesFood = servesFood;
    }

    public Cafe(String name, String location, String status, String imageUrl, String placeId) {
        this.name = name;
        this.location = location;
        this.status = status;
        this.imageUrl = imageUrl;
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public boolean hasWifi() {
        return hasWifi;
    }

    public boolean isDetails() {
        return isDetails;
    }

    public boolean isFoodPlace() {
        return isFoodPlace;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getImage() {
        return image;
    }

    public float getRating() {
        return rating;
    }

    public boolean servesFood() {
        return servesFood;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public void setQuiet(boolean isDetails) {
        this.isDetails = isDetails;
    }

    public void setFoodPlace(boolean isFoodPlace) {
        this.isFoodPlace = isFoodPlace;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setServesFood(boolean servesFood) {
        this.servesFood = servesFood;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
