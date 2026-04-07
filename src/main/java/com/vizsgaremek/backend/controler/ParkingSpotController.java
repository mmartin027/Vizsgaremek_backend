package com.vizsgaremek.backend.controler;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Zone;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.ZoneRepository;
import com.vizsgaremek.backend.service.ParkingSpotService;
import com.vizsgaremek.backend.service.ZoneService;
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

    @Autowired
    private ZoneRepository zoneRepository;

    @GetMapping("/search")
    public ResponseEntity<List<ParkingSpotDto>> search(@RequestParam Integer cityId) {
        return ResponseEntity.ok(service.searchByCity(cityId));
    }


    @GetMapping("/{identifier}")
    public ResponseEntity<ParkingSpotDto> getByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(service.getByIdentifier(identifier));
    }

    @GetMapping("/map-zones")
    public ResponseEntity<?> getMapZones() {
        try {
            List<ParkingSpot> spots = parkingSpotRepository.findAll();
            List<Zone> zones = zoneRepository.findAll();

            Map<String, Object> geoJson = new HashMap<>();
            geoJson.put("type", "FeatureCollection");
            List<Map<String, Object>> features = new ArrayList<>();


            for (Zone zone : zones) {
                if (zone.getPolygonData() == null || zone.getPolygonData().isEmpty()) {
                    continue;
                }

                Map<String, Object> feature = new HashMap<>();
                feature.put("type", "Feature");
                feature.put("id", zone.getId());

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> geometry = mapper.readValue(
                            zone.getPolygonData(),
                            new TypeReference<Map<String, Object>>() {}
                    );
                    feature.put("geometry", geometry);
                } catch (Exception e) {
                    System.err.println("Polygon parse hiba zone " + zone.getId() + ": " + e.getMessage());
                    continue;
                }

                Map<String, Object> properties = new HashMap<>();
                properties.put("id", zone.getId());
                properties.put("name", zone.getName());
                properties.put("zoneCode", zone.getZoneCode());
                properties.put("hourlyRate", zone.getHourlyRate());
                properties.put("mapId", zone.getMapId());
                properties.put("featureKind", "zone");  // <-- Frontend szűréshez!
                properties.put("parkingType", "ZONE");

                feature.put("properties", properties);
                features.add(feature);
            }


            for (ParkingSpot spot : spots) {
                if (!spot.getIsActive() || spot.getLatitude() == null || spot.getLongitude() == null) {
                    continue;
                }

                if (spot.getZone() != null) {
                    continue;
                }

                Map<String, Object> feature = new HashMap<>();
                feature.put("type", "Feature");
                feature.put("id", "spot-" + spot.getId());

                Map<String, Object> geometry = new HashMap<>();
                geometry.put("type", "Point");
                geometry.put("coordinates", Arrays.asList(
                        spot.getLongitude().doubleValue(),
                        spot.getLatitude().doubleValue()
                ));
                feature.put("geometry", geometry);

                Map<String, Object> properties = new HashMap<>();
                properties.put("id", spot.getId());
                properties.put("uuid", spot.getUuid());
                properties.put("name", spot.getName());
                properties.put("featureKind", "spot");  // <-- Frontend szűréshez!

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
                properties.put("parkingType", spot.getParkingType() != null ? spot.getParkingType().toString() : "OUTDOOR");
                properties.put("features", spot.getFeatures());
                properties.put("rating", spot.getRating());

                int capacity = spot.getCapacity() != null ? spot.getCapacity() : 0;
                int occupied = spot.getOccupiedSpaces() != null ? spot.getOccupiedSpaces() : 0;
                properties.put("capacity", capacity);
                properties.put("occupiedSpaces", occupied);
                properties.put("availableSpaces", Math.max(0, capacity - occupied));

                String imageUrl = spot.getMainImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                    imageUrl = "http://localhost:8080/images/" + imageUrl;
                }
                properties.put("imageUrl", imageUrl);

                feature.put("properties", properties);
                features.add(feature);
            }

            geoJson.put("features", features);
            System.out.println("Map zones: " + features.size() + " feature (zónák + parkolók)");
            return ResponseEntity.ok(geoJson);

        } catch (Exception e) {
            System.err.println("Map zones hiba: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Térkép adatok lekérése sikertelen: " + e.getMessage()));
        }
    }
    }


