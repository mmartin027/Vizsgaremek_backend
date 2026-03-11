package com.vizsgaremek.backend.model;

import com.vizsgaremek.backend.model.City;
import com.vizsgaremek.backend.model.Zone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.vizsgaremek.backend.model.Zone;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "parking_spots")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;


    @Size(max = 200)
    @Column(name = "name", length = 200)
    private String name;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;


    @Column(name = "hourly_rate")
    private Integer hourlyRate;

    @Column(name = "daily_rate")
    private Integer dailyRate;

    @Column(name = "monthly_rate")
    private Integer monthlyRate;

    @Column(name = "distance_from_center")
    private Integer distanceFromCenter;


    @Column(name = "types")
    private String types;

    @ColumnDefault("'OUTDOOR'")
    @Column(name = "parking_type")
    private String parkingType;

    @Lob
    @Column(name = "features")
    private String features;

    @Column(name = "capacity")
    private Integer capacity;

    @ColumnDefault("0")
    @Column(name = "occupied_spaces")
    private Integer occupiedSpaces;

    @Lob
    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Lob
    @Column(name = "image_gallery")
    private String imageGallery;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @ColumnDefault("0")
    @Column(name = "rating_count")
    private Integer ratingCount;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;


    @Column(name = "uuid", length = 36, unique = true)
    private String uuid;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

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