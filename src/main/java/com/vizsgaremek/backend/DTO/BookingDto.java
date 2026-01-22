package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.Booking}
 */
@Value
public class BookingDto implements Serializable {
    private Integer id;

    private Integer parkingSpotId;
    private Integer userId;

    private String parkingSpotName;
    private String userName;
    private String userEmail;

    private Instant startTime;
    private Instant endTime;
    private Integer hours;
    private Integer totalPrice;

    private String licensePlate;
    private String carBrand;
    private String carModel;
    private String carColor;

    private String status;
    private String qrCode;
    private String accessCode;
    private Boolean isExtended;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
}