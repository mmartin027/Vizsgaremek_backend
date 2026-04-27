package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.mapper.ZoneMapper;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Zone;
import com.vizsgaremek.backend.repository.CityRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CityRepository cityRepository;

    public Optional<ZoneDto> getZoneByZoneCode(String zoneCode) {
        return zoneRepository.findByZoneCode(zoneCode)
                .map(zoneMapper::toDto);
    }



    public Zone saveZone(ZoneDto zoneDto) {

        Zone zone = new Zone();
        zone.setName(zoneDto.getName());
        zone.setZoneCode(zoneDto.getZoneCode());
        Zone savedZone = zoneRepository.save(zone);


        ParkingSpot dummySpot = new ParkingSpot();
        dummySpot.setName(savedZone.getName() + " (Utcai zóna)");
        dummySpot.setAddress(savedZone.getName() + " területe");
        dummySpot.setHourlyRate(savedZone.getHourlyRate());
        dummySpot.setFeatures(savedZone.getFeatures());
        dummySpot.setZone(savedZone);
        dummySpot.setIsActive(true);

        parkingSpotRepository.save(dummySpot);

        return savedZone;
    }

    public List<ZoneDto> getAllZones() {
        List<Zone> zones = zoneRepository.findAll();
        return zones.stream().map(zone -> {
            ZoneDto dto = new ZoneDto();
            dto.setId(zone.getId());
            dto.setName(zone.getName());
            dto.setZoneCode(zone.getZoneCode());
            dto.setHourlyRate(zone.getHourlyRate());
            dto.setPolygonData(zone.getPolygonData());
            dto.setFeatures(zone.getFeatures());
            return dto;
        }).collect(Collectors.toList());
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

        Integer cityId = detectCityFromZoneCode(savedZone.getZoneCode());
        if (cityId != null) {
            cityRepository.findById(cityId).ifPresent(spot::setCity);
        }

        parkingSpotRepository.save(spot);
        return zoneMapper.toDto(savedZone);
    }

    private Integer detectCityFromZoneCode(String zoneCode) {
        if (zoneCode == null || zoneCode.isEmpty()) return null;
        String prefix = zoneCode.substring(0, 1).toUpperCase();
        switch (prefix) {
            case "A": return 2;
            case "B": return 1;
            case "D": return 3;
            case "E": return 4;
            default: return null;
        }
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

    public void updatePrice(Integer id, Integer newPrice) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zóna nem található!"));
        zone.setHourlyRate(newPrice);
        zoneRepository.save(zone);

        List<ParkingSpot> spots = parkingSpotRepository.findByZoneId(id);
        for (ParkingSpot spot : spots) {
            spot.setHourlyRate(newPrice);
            parkingSpotRepository.save(spot);
        }
    }


    public void updateImageUrl(Integer zoneId, String fileName) {
        ParkingSpot zoneSpot = parkingSpotRepository.findAll().stream()
                .filter(s -> s.getZone() != null && s.getZone().getId().equals(zoneId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Kritikus hiba: Nem található a zónához tartozó főparkoló!"));

        zoneSpot.setMainImageUrl(fileName);

        parkingSpotRepository.save(zoneSpot);
    }


}