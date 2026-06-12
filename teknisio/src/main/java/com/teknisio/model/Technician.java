package com.teknisio.model;

/**
 * Model class representing a Technician.
 */
public class Technician {
    private String name;
    private String specialization;
    private double rating;
    private String priceRange;
    private String avatarPath;
    private boolean isFeatured;

    public Technician(String name, String specialization, double rating, String priceRange, String avatarPath, boolean isFeatured) {
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
        this.priceRange = priceRange;
        this.avatarPath = avatarPath;
        this.isFeatured = isFeatured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
}
