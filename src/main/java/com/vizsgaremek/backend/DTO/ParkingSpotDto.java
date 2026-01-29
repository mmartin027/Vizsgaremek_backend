package com.vizsgaremek.backend.DTO;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.ParkingSpot}
 */
@Data
public class ParkingSpotDto implements Serializable {

    // Alapadatok
    private Integer id;
    private String name;
    private String address;

    // Lokáció
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Város adatok
    private Integer cityId;
    private String cityName;

    // Típus és zóna
    private String parkingType;
    private String zoneName;
    private String zoneCode;

    // Árazás
    private Integer hourlyRate;
    private Integer dailyRate;
    private Integer monthlyRate;

    // Kapacitás
    private Integer capacity;
    private Integer occupiedSpaces;
    private Integer availableSpaces;   // Számított mező: capacity - occupiedSpaces

    // Távolság
    private Integer distanceFromCenter;

    // Tulajdonságok
    private String features;
    private String description;

    // Képek
    private String mainImageUrl;
    private String imageGallery;

    // Értékelés
    private BigDecimal rating;
    private Integer ratingCount;

    // Aktív-e
    private Boolean isActive;

    // Időbélyegek
    private Instant createdAt;
    private Instant updatedAt;

    public ParkingSpotDto(Integer id, String name, String address, Integer hourlyRate, String features, String fullImageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.features = features;
        this.mainImageUrl = mainImageUrl;

    }
}