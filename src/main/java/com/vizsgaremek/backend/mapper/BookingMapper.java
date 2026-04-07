package com.vizsgaremek.backend.mapper;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());

        if (booking.getParkingSpot() != null) {
            dto.setParkingSpotId(booking.getParkingSpot().getId());
            dto.setParkingSpotName(booking.getParkingSpot().getName());
            dto.setParkingSpotAddress(booking.getParkingSpot().getAddress());
        }

        if (booking.getUser() != null) {
            dto.setUserId((booking.getUser().getId()));
            dto.setUserName(booking.getUser().getUsername());
            dto.setUserEmail(booking.getUser().getEmail());
        }

        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setHours(booking.getHours());
        dto.setTotalPrice(booking.getTotalPrice());

        dto.setLicensePlate(booking.getLicensePlate());
        dto.setCarBrand(booking.getCarBrand());
        dto.setCarModel(booking.getCarModel());
        dto.setCheckInTime(booking.getCheckInTime());
        dto.setCarColor(booking.getCarColor());
        dto.setParkingType(booking.getParkingType());

        dto.setStatus(booking.getStatus());
        dto.setQrCode(booking.getQrCode());
        dto.setAccessCode(booking.getAccessCode());
        dto.setIsExtended(booking.getIsExtended());

        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setCancelledAt(booking.getCancelledAt());

        return dto;
    }
}