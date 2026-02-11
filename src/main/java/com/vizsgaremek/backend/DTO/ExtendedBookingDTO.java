package com.vizsgaremek.backend.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ExtendedBookingDTO {

    @Min(1)
    @Max(24)
     private Integer additionalMinutes;

}
