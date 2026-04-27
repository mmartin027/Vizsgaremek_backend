package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository repository;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private ParkingSpotDto convertToDto(ParkingSpot spot) {
        String fullImageUrl = null;

        if (spot.getMainImageUrl() != null && !spot.getMainImageUrl().isEmpty()) {
            if (!spot.getMainImageUrl().startsWith("http")) {
                fullImageUrl = baseUrl + "/images/" + spot.getMainImageUrl();
            } else {
                fullImageUrl = spot.getMainImageUrl();
            }
        }

        ParkingSpotDto dto = new ParkingSpotDto(
                spot.getId(),
                spot.getUuid(),
                spot.getName(),
                spot.getAddress(),
                spot.getZone() != null ? spot.getZone().getHourlyRate() : spot.getHourlyRate(),
                spot.getFeatures(),
                fullImageUrl,
                spot.getZone() != null ? spot.getZone().getName() : null,
                spot.getZone() != null ? spot.getZone().getZoneCode() : null,
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getCity() != null ? spot.getCity().getId() : null,
                spot.getCity() != null ? spot.getCity().getName() : null
        );

        dto.setParkingType(spot.getParkingType());

        Integer baseRate = spot.getZone() != null && spot.getZone().getHourlyRate() != null
                ? spot.getZone().getHourlyRate()
                : spot.getHourlyRate();

        if (baseRate != null) {
            int calculatedDaily = (int) (Math.round(baseRate * 7.0 / 100.0) * 100);
            dto.setDailyRate(spot.getDailyRate() != null ? spot.getDailyRate() : calculatedDaily);

            int calculatedMonthly = (int) (Math.round(baseRate * 40.0 / 100.0) * 100);
            dto.setMonthlyRate(spot.getMonthlyRate() != null ? spot.getMonthlyRate() : calculatedMonthly);
        }

        int activeBookings = (int) bookingRepository.countActiveBookings(spot.getId());
        dto.setOccupiedSpaces(activeBookings);

        if (spot.getZone() != null) {
            dto.setCapacity(9999);
            dto.setAvailableSpaces(9999);
        } else {
            int capacity = spot.getCapacity() != null ? spot.getCapacity() : 0;
            dto.setCapacity(capacity);
            dto.setAvailableSpaces(Math.max(0, capacity - activeBookings));
        }

        return dto;
    }

    private ParkingSpotDto convertToDto(ParkingSpot spot, Map<Integer, Long> activeBookingsMap) {
        String fullImageUrl = null;

        if (spot.getMainImageUrl() != null && !spot.getMainImageUrl().isEmpty()) {
            if (!spot.getMainImageUrl().startsWith("http")) {
                fullImageUrl = baseUrl + "/images/" + spot.getMainImageUrl();
            } else {
                fullImageUrl = spot.getMainImageUrl();
            }
        }

        ParkingSpotDto dto = new ParkingSpotDto(
                spot.getId(),
                spot.getUuid(),
                spot.getName(),
                spot.getAddress(),
                spot.getZone() != null ? spot.getZone().getHourlyRate() : spot.getHourlyRate(),
                spot.getFeatures(),
                fullImageUrl,
                spot.getZone() != null ? spot.getZone().getName() : null,
                spot.getZone() != null ? spot.getZone().getZoneCode() : null,
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getCity() != null ? spot.getCity().getId() : null,
                spot.getCity() != null ? spot.getCity().getName() : null
        );

        dto.setParkingType(spot.getParkingType());

        Integer baseRate = spot.getZone() != null && spot.getZone().getHourlyRate() != null
                ? spot.getZone().getHourlyRate()
                : spot.getHourlyRate();

        if (baseRate != null) {
            int calculatedDaily = (int) (Math.round(baseRate * 7.0 / 100.0) * 100);
            dto.setDailyRate(spot.getDailyRate() != null ? spot.getDailyRate() : calculatedDaily);

            int calculatedMonthly = (int) (Math.round(baseRate * 40.0 / 100.0) * 100);
            dto.setMonthlyRate(spot.getMonthlyRate() != null ? spot.getMonthlyRate() : calculatedMonthly);
        }

        int activeBookings = activeBookingsMap.getOrDefault(spot.getId(), 0L).intValue();
        dto.setOccupiedSpaces(activeBookings);

        if (spot.getZone() != null) {
            dto.setCapacity(9999);
            dto.setAvailableSpaces(9999);
        } else {
            int capacity = spot.getCapacity() != null ? spot.getCapacity() : 0;
            dto.setCapacity(capacity);
            dto.setAvailableSpaces(Math.max(0, capacity - activeBookings));
        }

        return dto;
    }

    public List<ParkingSpotDto> searchByCity(Integer cityId) {
        List<ParkingSpot> spots = repository.findByCityIdWithZoneAndCity(cityId);

        List<Object[]> activeCounts = bookingRepository.countActiveBookingsPerSpot();
        Map<Integer, Long> activeBookingsMap = new HashMap<>();
        for (Object[] row : activeCounts) {
            activeBookingsMap.put((Integer) row[0], (Long) row[1]);
        }

        return spots.stream()
                .map(spot -> convertToDto(spot, activeBookingsMap))
                .collect(Collectors.toList());
    }

    public void updateImageUrl(Integer spotId, String fileName) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("A parkoló nem található ezzel az ID-val: " + spotId));

        spot.setMainImageUrl(fileName);
        parkingSpotRepository.save(spot);
    }

    public void updatePrice(Integer id, Integer newPrice) {
        ParkingSpot spot = repository.findById(id).orElseThrow(() -> new RuntimeException("Parkoló nem található!"));
        spot.setHourlyRate(newPrice);
        repository.save(spot);
    }

    public List<ParkingSpotDto> searchByCityName(String cityName) {
        List<ParkingSpot> spots = repository.findByCity_NameContainingIgnoreCaseAndIsActiveTrue(cityName);

        List<Object[]> activeCounts = bookingRepository.countActiveBookingsPerSpot();
        Map<Integer, Long> activeBookingsMap = new HashMap<>();
        for (Object[] row : activeCounts) {
            activeBookingsMap.put((Integer) row[0], (Long) row[1]);
        }

        return spots.stream()
                .map(spot -> convertToDto(spot, activeBookingsMap))
                .collect(Collectors.toList());
    }

    public ParkingSpotDto getByIdentifier(String identifier) {
        ParkingSpot spot = null;

        try {
            Integer id = Integer.parseInt(identifier);
            spot = repository.findById(id).orElse(null);

            if (spot == null) {
                spot = repository.findFirstByZoneId(id)
                        .orElseThrow(() -> new RuntimeException("A parkoló/zóna nem található ezzel az azonosítóval (se parkolóként, se zónaként): " + id));
            }

        } catch (NumberFormatException e) {
            spot = repository.findByUuid(identifier)
                    .orElseThrow(() -> new RuntimeException("A parkolóhely nem található ezzel az UUID-vel: " + identifier));
        }

        return convertToDto(spot);
    }
}