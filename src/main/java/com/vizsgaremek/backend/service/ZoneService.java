package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.mapper.ZoneMapper;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Zone;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;
    private final ParkingSpotRepository parkingSpotRepository;

    public Optional<ZoneDto> getZoneByZoneCode(String zoneCode) {
        return zoneRepository.findByZoneCode(zoneCode)
                .map(zoneMapper::toDto);
    }

    public List<ZoneDto> getAllZones() {
        List<Zone> zones = zoneRepository.findAll();
        return zones.stream().map(zone -> new ZoneDto(
                zone.getId(),
                zone.getName(),
                zone.getZoneCode(),
                zone.getHourlyRate(),
                zone.getPolygonData(),
                zone.getFeatures()
        )).collect(Collectors.toList());
    }

    public ZoneDto createZone(ZoneDto zoneDto) {
        Zone zone = new Zone();
        zone.setName(zoneDto.getName());
        zone.setZoneCode(zoneDto.getZoneCode());
        zone.setHourlyRate(zoneDto.getHourlyRate());
        zone.setFeatures(zoneDto.getFeatures());

        String polygonData = zoneDto.getPolygonData();
        if (polygonData != null && !polygonData.contains("\"type\"")) {
            polygonData = "{\"type\":\"Polygon\",\"coordinates\":[" + polygonData + "]}";
        }
        zone.setPolygonData(polygonData);

        Zone savedZone = zoneRepository.save(zone);

        ParkingSpot spot = new ParkingSpot();
        spot.setName(savedZone.getName());
        spot.setZone(savedZone);
        spot.setHourlyRate(savedZone.getHourlyRate());
        spot.setParkingType("OUTDOOR");
        spot.setIsActive(true);
        spot.setFeatures(savedZone.getFeatures());
        spot.setOccupiedSpaces(0);
        spot.setUuid(UUID.randomUUID().toString());
        parkingSpotRepository.save(spot);

        return zoneMapper.toDto(savedZone);
    }

    @Transactional
    public void deleteZone(Integer id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("A zóna nem található ezzel az ID-val: " + id);
        }
        List<ParkingSpot> spots = parkingSpotRepository.findByZoneId(id);
        for (ParkingSpot spot : spots) {
            parkingSpotRepository.delete(spot);
        }
        zoneRepository.deleteById(id);
    }
}