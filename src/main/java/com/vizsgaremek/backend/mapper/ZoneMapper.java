package com.vizsgaremek.backend.mapper;

import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.model.Zone;
import org.springframework.stereotype.Component;

@Component
public class ZoneMapper {

    public ZoneDto toDto(Zone zone) {
        if (zone == null) return null;
        ZoneDto dto = new ZoneDto();
        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setZoneCode(zone.getZoneCode());
        dto.setHourlyRate(zone.getHourlyRate());
        dto.setPolygonData(zone.getPolygonData());
        dto.setFeatures(zone.getFeatures());
        return dto;
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