package com.vizsgaremek.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.vizsgaremek.backend.model.ParkingSpace}
 */
@Value
public class ParkingSpaceDto implements Serializable {
    Integer id;
    String code;
    Boolean isOccupied;
    String type;
    String size;
    Instant createdAt;
    Instant updatedAt;
}