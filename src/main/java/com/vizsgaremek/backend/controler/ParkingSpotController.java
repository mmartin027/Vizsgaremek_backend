package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;

    @GetMapping("/search")
    public ResponseEntity<List<ParkingSpotDto>> search(@RequestParam Integer cityId) {
        return ResponseEntity.ok(service.searchByCity(cityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }


}
