package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.City}
 */
@Value
public class CityDto implements Serializable {
    Integer id;
    String name;
    BigDecimal latitude;
    BigDecimal longitude;
    Boolean isActive;
    Instant createdAt;
}