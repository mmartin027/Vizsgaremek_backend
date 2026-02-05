package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository repository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;



    public List<ParkingSpotDto> searchByCity(Integer cityId) {
        return repository.findByCityIdAndIsActiveTrue(cityId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ParkingSpotDto convertToDto(ParkingSpot spot) {
        String fullImageUrl = null;

        if (spot.getMainImageUrl() != null && !spot.getMainImageUrl().isEmpty()) {
            if (!spot.getMainImageUrl().startsWith("http")) {
                fullImageUrl = baseUrl + "/images/" + spot.getMainImageUrl();
            } else {
                fullImageUrl = spot.getMainImageUrl();
            }
        }

        return new ParkingSpotDto(
                spot.getId(),
                spot.getName(),
                spot.getAddress(),
                spot.getHourlyRate(),
                spot.getFeatures(),
                fullImageUrl
        );
    }

    public ParkingSpotDto getById(Integer id) {

        ParkingSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("A parkolóhely nem található ezzel az azonosítóval: " + id));


        return convertToDto(spot);
    }
}