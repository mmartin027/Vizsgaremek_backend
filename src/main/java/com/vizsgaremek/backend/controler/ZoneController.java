package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/map/{mapId}")
    public ResponseEntity<ZoneDto> getZoneByMapId(@PathVariable String mapId) {
        return zoneService.getZoneByZoneCode(mapId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

