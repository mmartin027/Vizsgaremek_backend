package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.vizsgaremek.backend.model.ParkingSpot}
 */
@Value
public class ParkingSpotDto implements Serializable {
    Integer id;
    String Address;
    String name;
    Integer hourlyRate;
    String features;
    String mainImageUrl;
}