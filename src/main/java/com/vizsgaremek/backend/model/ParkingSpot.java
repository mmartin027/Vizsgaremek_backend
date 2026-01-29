package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

    @Column(name = "name", length = 200)
    private String name;

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
    @Lob
    @Column(name = "parking_type")
    private String parkingType;

    @Column(name = "zone_name", length = 100)
    private String zoneName;

    @Column(name = "zone_code", length = 20)
    private String zoneCode;

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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @OneToMany(mappedBy = "parkingSpot")
    private List<Booking> bookings;

    public String getMainImageUrl() {
        return mainImageUrl;
    }


}