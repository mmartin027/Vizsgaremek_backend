package com.vizsgaremek.backend.DTO;

import lombok.Data;
import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.Booking}
 */
@Data
public class BookingDto implements Serializable {

    // Booking alapadatok
    private Integer id;

    // ParkingSpot adatok
    private Integer parkingSpotId;
    private String parkingSpotName;
    private String parkingSpotAddress;
    private String parkingType;        // COVERED vagy OUTDOOR
    private String zoneName;           // I. Zóna (ha outdoor)

    // User adatok
    private Integer userId;
    private String userName;
    private String userEmail;

    // Időpontok
    private Instant startTime;
    private Instant endTime;
    private Integer hours;
    private Integer totalPrice;

    // Autó adatok
    private String licensePlate;
    private String carBrand;
    private String carModel;
    private String carColor;

    // Státusz
    private String status;
    private String qrCode;
    private String accessCode;

    // Meghosszabbítás
    private Boolean isExtended;
    private Integer extensionCount;
    private Instant originalEndTime;
    private Instant lastExtendedAt;

    // Check-in
    private Instant checkInTime;
    private Instant checkOutTime;

    // Időbélyegek
    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
}