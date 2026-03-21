package com.vizsgaremek.backend.service;


import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.mapper.ZoneMapper;
import com.vizsgaremek.backend.model.Zone;
import com.vizsgaremek.backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public Optional<ZoneDto> getZoneByMapId(String mapId) {
        return zoneRepository.findByMapId(mapId)
                .map(zoneMapper::toDto);
    }
    public List<ZoneDto> getAllZones() {
        List<Zone> zones = zoneRepository.findAll();

        return zones.stream().map(zone -> new ZoneDto(
                zone.getId(),
                zone.getName(),
                zone.getZoneCode(),
                zone.getHourlyRate(),
                zone.getMapId(),
                zone.getPolygonData()
        )).collect(Collectors.toList());
    }

    public ZoneDto createZone(ZoneDto zoneDto) {
        Zone zone = new Zone();
        zone.setName(zoneDto.getName());
        zone.setZoneCode(zoneDto.getZoneCode());
        zone.setHourlyRate(zoneDto.getHourlyRate());
        zone.setMapId(zoneDto.getMapId());
        zone.setPolygonData(zoneDto.getPolygonData());

         //Mentés az adatbázisba
        Zone savedZone = zoneRepository.save(zone);

        return new ZoneDto(
                savedZone.getId(),
                savedZone.getName(),
                savedZone.getZoneCode(),
                savedZone.getHourlyRate(),
                savedZone.getMapId(),
                savedZone.getPolygonData()
        );
    }

    public void deleteZone(Integer id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("A zóna nem található ezzel az ID-val: " + id);
        }
        zoneRepository.deleteById(id);
    }
    }

