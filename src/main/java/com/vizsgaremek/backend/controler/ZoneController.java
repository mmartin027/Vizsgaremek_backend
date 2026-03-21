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
@CrossOrigin(origins = "*") // Fejlesztés alatt engedélyezz
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/map/{mapId}")
    public ResponseEntity<ZoneDto> getZoneByMapId(@PathVariable String mapId) {
        return zoneService.getZoneByMapId(mapId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    // 2. ÚJ ZÓNA MENTÉSE (Amikor rajzolsz a térképen)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ZoneDto> createZone(@RequestBody ZoneDto zoneDto) {
        ZoneDto savedZone = zoneService.createZone(zoneDto);
        return ResponseEntity.ok(savedZone);
    }

    // 3. ZÓNA TÖRLÉSE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteZone(@PathVariable Integer id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok("Zóna sikeresen törölve!");
    }
}


