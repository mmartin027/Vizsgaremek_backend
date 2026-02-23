package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @GetMapping("/search")
    public ResponseEntity<List<ParkingSpotDto>> search(@RequestParam Integer cityId) {
        return ResponseEntity.ok(service.searchByCity(cityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

        @GetMapping("/map-zones")
        public ResponseEntity<?> getMapZones() {
            try {
                List<ParkingSpot> spots = parkingSpotRepository.findAll();

                // GeoJSON FeatureCollection létrehozása
                Map<String, Object> geoJson = new HashMap<>();
                geoJson.put("type", "FeatureCollection");

                List<Map<String, Object>> features = new ArrayList<>();

                for (ParkingSpot spot : spots) {
                    // Csak aktív és geolokációval rendelkező spotok
                    if (!spot.getIsActive() || spot.getLatitude() == null || spot.getLongitude() == null) {
                        continue;
                    }

                    Map<String, Object> feature = new HashMap<>();
                    feature.put("type", "Feature");

                    // Geometry (pont koordináták)
                    Map<String, Object> geometry = new HashMap<>();
                    geometry.put("type", "Point");
                    geometry.put("coordinates", Arrays.asList(
                            spot.getLongitude().doubleValue(),  // lng először!
                            spot.getLatitude().doubleValue()    // lat másodszor!
                    ));
                    feature.put("geometry", geometry);

                    // Properties (adatok a térképhez)
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("id", spot.getId());
                    properties.put("name", spot.getName());
                    if (spot.getZone() != null) {
                        properties.put("zoneName", spot.getZone().getName());
                        properties.put("zoneCode", spot.getZone().getZoneCode());
                        properties.put("hourlyRate", spot.getZone().getHourlyRate());
                    } else {
                        properties.put("zoneName", "Nincs zóna");
                        properties.put("zoneCode", "N/A");
                        properties.put("hourlyRate", spot.getHourlyRate());
                    }
                    properties.put("address", spot.getAddress());
                    properties.put("hourlyRate", spot.getHourlyRate());
                    properties.put("capacity", spot.getCapacity());
                    properties.put("occupiedSpaces", spot.getOccupiedSpaces());
                    properties.put("availableSpaces", spot.getCapacity() - spot.getOccupiedSpaces());
                    properties.put("parkingType", spot.getParkingType() != null ? spot.getParkingType().toString() : "OUTDOOR");
                    properties.put("features", spot.getFeatures());
                    properties.put("rating", spot.getRating());
                    properties.put("imageUrl", spot.getMainImageUrl());

                    feature.put("properties", properties);
                    features.add(feature);
                }

                geoJson.put("features", features);

                System.out.println(" Map zones: " + features.size() + " parkolóhely");

                return ResponseEntity.ok(geoJson);

            } catch (Exception e) {
                System.err.println(" Map zones hiba: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Térkép adatok lekérése sikertelen: " + e.getMessage()));
            }
        }
    }


