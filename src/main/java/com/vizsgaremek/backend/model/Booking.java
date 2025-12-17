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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace parkingSpace;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime; // ⚠ default törölve

    private Integer hours;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(length = 20)
    private String licensePlate;

    private String carBrand;
    private String carModel;

    @Column(length = 50)
    private String carColor;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "active";  // ⚠ TEXT default helyett

    @Lob
    private String qrCode;

    @Column(length = 10)
    private String accessCode;

    @Lob
    private String note;

    @Lob
    private String cancellationReason;

    private Boolean isExtended = false; // ⚠ default kijavítva

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(nullable = false)
    private Instant updatedAt;

    private Instant cancelledAt;
}
