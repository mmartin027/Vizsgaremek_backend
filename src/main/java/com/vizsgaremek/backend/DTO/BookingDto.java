package com.vizsgaremek.backend.DTO;

import lombok.Data;
import java.io.Serializable;
import java.time.Instant;


@Data
public class BookingDto implements Serializable {

    private Integer id;


    private Integer parkingSpotId;
    private String parkingSpotName;
    private String parkingSpotAddress;
    private String parkingType;
    private String zoneName;

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


    private Boolean isExtended;
    private Integer extensionCount;
    private Instant originalEndTime;
    private Instant lastExtendedAt;

    private Instant checkInTime;
    private Instant checkOutTime;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
}