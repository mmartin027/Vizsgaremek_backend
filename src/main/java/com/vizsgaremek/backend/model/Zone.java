package com.vizsgaremek.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 25)
    @Column(name = "zone_code", length = 25)
    private String zoneCode;

    @Column(name = "hourly_rate")
    private Integer hourlyRate;

    @Size(max = 50)
    @Column(name = "map_id", length = 50)
    private String mapId;

    @Column(columnDefinition = "TEXT")
    private String polygonData;

}