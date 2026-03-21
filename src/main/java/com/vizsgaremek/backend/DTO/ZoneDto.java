package com.vizsgaremek.backend.DTO;

import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.vizsgaremek.backend.model.Zone}
 */
@Value
public class ZoneDto implements Serializable {
    Integer id;
    @Size(max = 50)
    String name;
    @Size(max = 25)
    String zoneCode;
    Integer hourlyRate;
    @Size(max = 50)
    String mapId;
    String polygonData;
}