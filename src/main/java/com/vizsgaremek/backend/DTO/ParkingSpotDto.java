package com.vizsgaremek.backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor // Szükséges a keretrendszereknek
public class ParkingSpotDto implements Serializable {

    private Integer id;
    private String name;
    private String address;

    // Lokáció
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Város és Zóna azonosítók
    private Integer cityId;
    private String cityName;
    private Integer zoneId;      // ÚJ: Az adatbázis kapcsolathoz
    private String zoneMapId;   // ÚJ: A MapTiler-es összekötéshez

    // Típus és zóna megjelenítés
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
    private Integer availableSpaces;

    // Tulajdonságok és leírás
    private String features;
    private String description;
    private String main_Image_Url;
    private String imageGallery;

    // Értékelés és állapot
    private BigDecimal rating;
    private Integer ratingCount;
    private Boolean isActive;

    private String uuid;
    // Időbélyegek
    private Instant createdAt;
    private Instant updatedAt;

    // FRISSÍTETT KONSTRUKTOR a Service-hez
    public ParkingSpotDto(Integer id, String uuid, String name, String address, Integer hourlyRate,
                          String features, String fullImageUrl, String zoneName, String zoneCode) {
        this.id = id;
        this.uuid = uuid; // <-- ÚJ
        this.name = name;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.features = features;
        this.main_Image_Url = fullImageUrl;
        this.zoneName = zoneName;
        this.zoneCode = zoneCode;
    }
}