package com.vizsgaremek.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.Booking}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto implements Serializable {
    private Integer id;

    private Integer parkingSpotId;
    private String parkingSpotName;
    private String parkingSpotAddress;

    // User kapcsolat
    private Integer userId;
    private String userName;
    private String userEmail;

    // Foglalás idő adatok
    private Instant startTime;
    private Instant endTime;
    private Integer hours;
    private Integer totalPrice;

    // Autó adatok
    private String licensePlate;
    private String carBrand;
    private String carModel;
    private String carColor;

    // Státusz és extra info
    private String status;
    private String qrCode;
    private String accessCode;
    private Boolean isExtended;

    // Időbélyegek
    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
}