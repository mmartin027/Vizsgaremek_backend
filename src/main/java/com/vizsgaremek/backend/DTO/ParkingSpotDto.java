package com.vizsgaremek.backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class ParkingSpotDto implements Serializable {

    private Integer id;
    private String name;
    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Integer cityId;
    private String cityName;
    private Integer zoneId;
    private String zoneMapId;

    private String parkingType;
    private String zoneName;
    private String zoneCode;

    private Integer hourlyRate;
    private Integer dailyRate;
    private Integer monthlyRate;

    private Integer capacity;
    private Integer occupiedSpaces;
    private Integer availableSpaces;

    private String features;
    private String description;
    private String main_Image_Url;
    private String imageGallery;

    private BigDecimal rating;
    private Integer ratingCount;
    private Boolean isActive;

    private String uuid;

    private Instant createdAt;
    private Instant updatedAt;

    public ParkingSpotDto(Integer id, String uuid, String name, String address, Integer hourlyRate,
                          String features, String fullImageUrl, String zoneName, String zoneCode,
                          BigDecimal latitude, BigDecimal longitude, Integer cityId, String cityName) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.features = features;
        this.main_Image_Url = fullImageUrl;
        this.zoneName = zoneName;
        this.zoneCode = zoneCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityId = cityId;
        this.cityName = cityName;
    }
}