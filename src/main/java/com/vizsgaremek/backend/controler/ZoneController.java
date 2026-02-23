package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Fejlesztés alatt engedélyezz
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/map/{mapId}")
    public ResponseEntity<ZoneDto> getZoneByMapId(@PathVariable String mapId) {
        return zoneService.getZoneByMapId(mapId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
