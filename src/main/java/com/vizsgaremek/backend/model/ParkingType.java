package com.vizsgaremek.backend.model;

public enum ParkingType {
    COVERED("Fedett parkolóház"),
    OUTDOOR("Kültéri zónás parkolás");

    private final String displayName;

    ParkingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}