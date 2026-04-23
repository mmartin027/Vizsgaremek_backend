package com.vizsgaremek.backend.mapper;

import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.model.Zone;
import org.springframework.stereotype.Component;

@Component
public class ZoneMapper {

    public ZoneDto toDto(Zone zone) {
        if (zone == null) return null;

        return new ZoneDto(
                zone.getId(),
                zone.getName(),
                zone.getZoneCode(),
                zone.getHourlyRate(),
                zone.getPolygonData(),
                zone.getFeatures()
        );
    }

    public Zone toEntity(ZoneDto dto) {
        if (dto == null) return null;

        Zone zone = new Zone();
        zone.setId(dto.getId());
        zone.setName(dto.getName());
        zone.setZoneCode(dto.getZoneCode());
        zone.setHourlyRate(dto.getHourlyRate());
        zone.setPolygonData(dto.getPolygonData());
        zone.setFeatures(dto.getFeatures());
        return zone;
    }
}