package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_spot_id")
    private ParkingSpot parkingSpot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "hours")
    private Integer hours;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Column(name = "car_brand")
    private String carBrand;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "car_color", length = 50)
    private String carColor;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Lob
    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "access_code", length = 10)
    private String accessCode;

    @Lob
    @Column(name = "note")
    private String note;

    @Lob
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @ColumnDefault("0")
    @Column(name = "is_extended")
    private Boolean isExtended;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "parking_type", length = 20)
    private String parkingType;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @Column(name = "check_out_time")
    private Instant checkOutTime;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}