package com.vizsgaremek.backend.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private Long amount;
    private  Long quantity;
    private String name;
    private String currency;
    private Integer parkingSpotId;
    private Integer userId;
    private String startTime;
    private String endTime;
    private String licensePlate;
    private String carBrand;
    private String carModel;
    private String carColor;
    private String qrCode;
}



