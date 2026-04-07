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

    @Value("${app.base-url}")
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
                spot.getUuid(),
                spot.getName(),
                spot.getAddress(),
                spot.getZone() != null ? spot.getZone().getHourlyRate() : spot.getHourlyRate(),
                spot.getFeatures(),
                fullImageUrl,
                spot.getZone() != null ? spot.getZone().getName() : null,
                spot.getZone() != null ? spot.getZone().getZoneCode() : null
        );
    }

    public ParkingSpotDto getByIdentifier(String identifier) {
        ParkingSpot spot;

        try {
            Integer id = Integer.parseInt(identifier);
            spot = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("A parkolóhely nem található ezzel az azonosítóval: " + id));
        } catch (NumberFormatException e) {
            spot = repository.findByUuid(identifier)
                    .orElseThrow(() -> new RuntimeException("A parkolóhely nem található ezzel az UUID-vel: " + identifier));
        }

        return convertToDto(spot);
    }
}