package com.vizsgaremek.backend.DTO;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDto implements Serializable {
    private Integer id;
    @Size(max = 50)
    private String name;
    @Size(max = 25)
    private String zoneCode;
    private Integer hourlyRate;
    private String polygonData;
    private String features;
    private Integer cityId;

}