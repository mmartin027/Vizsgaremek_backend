package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 25)
    @Column(name = "zone_code", length = 25)
    private String zoneCode;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "hourly_rate")
    private Integer hourlyRate;


    @Column(columnDefinition = "TEXT")
    private String polygonData;

}