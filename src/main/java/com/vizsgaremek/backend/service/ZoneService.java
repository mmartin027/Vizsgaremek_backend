package com.vizsgaremek.backend.service;


import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.mapper.ZoneMapper;
import com.vizsgaremek.backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public Optional<ZoneDto> getZoneByMapId(String mapId) {
        return zoneRepository.findByMapId(mapId)
                .map(zoneMapper::toDto);
    }
}
