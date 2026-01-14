package com.vizsgaremek.backend.service;


import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository repository;

    public List<ParkingSpotDto> searchByCity(Integer cityId) {
        return repository.findByCityIdAndIsActiveTrue(cityId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ParkingSpotDto convertToDto(ParkingSpot spot) {
        return new ParkingSpotDto(
                spot.getId(),
                spot.getName(),
                spot.getHourlyRate(),
                spot.getFeatures(),
                spot.getMainImageUrl()
        );
    }

}
