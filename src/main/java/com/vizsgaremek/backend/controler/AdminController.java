package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.UserDto;
import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.Zone;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.ZoneRepository;
import com.vizsgaremek.backend.service.AdminService;
import com.vizsgaremek.backend.service.ParkingSpotService;
import com.vizsgaremek.backend.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ParkingSpotService parkingSpotService;
    private final ZoneService zoneService;
    private final ZoneRepository zoneRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Value("${app.upload.dir:uploads/images/}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private String saveFileLocally(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("A fájl üres!");

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Csak JPG, PNG és WebP képek engedélyezettek!");
        }

        String finalDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        File directory = new File(finalDir);
        if (!directory.exists()) directory.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String safeName = UUID.randomUUID().toString() + extension;

        Path filePath = Paths.get(finalDir + safeName);
        Files.write(filePath, file.getBytes());

        return safeName;
    }

    @PostMapping("/parking-spots/{id}/image")
    public ResponseEntity<?> uploadSpotImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = saveFileLocally(file);
            parkingSpotService.updateImageUrl(id, fileName);
            return ResponseEntity.ok(Map.of("message", "Parkoló képe sikeresen feltöltve!", "imageUrl", fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Hiba a feltöltés során: " + e.getMessage()));
        }
    }

    @PostMapping("/zones/{id}/image")
    public ResponseEntity<?> uploadZoneImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = saveFileLocally(file);
            zoneService.updateImageUrl(id, fileName);
            return ResponseEntity.ok(Map.of("message", "Zóna képe sikeresen feltöltve!", "imageUrl", fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Hiba a feltöltés során: " + e.getMessage()));
        }
    }

    @PutMapping("/parking-spots/{id}/price")
    public ResponseEntity<?> updateSpotPrice(@PathVariable Integer id, @RequestParam Integer price) {
        try {
            parkingSpotService.updatePrice(id, price);
            return ResponseEntity.ok(Map.of("message", "Parkoló ára sikeresen frissítve!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/zones/{id}/price")
    public ResponseEntity<?> updateZonePrice(@PathVariable Integer id, @RequestParam Integer price) {
        try {
            zoneService.updatePrice(id, price);
            return ResponseEntity.ok(Map.of("message", "Zóna ára sikeresen frissítve!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/parking-spots/{id}")
    public ResponseEntity<?> deleteParkingSpot(@PathVariable Integer id) {
        try {
            adminService.deleteParkingSpot(id);
            return ResponseEntity.ok("Parkolóhely sikeresen törölve.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Nem törölhető! A parkolóhoz már tartoznak korábbi foglalások a történetben.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<Page<Booking>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(adminService.getAllBookings(page, size));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer id) {
        try {
            adminService.adminCancelBooking(id);
            return ResponseEntity.ok("Foglalás lemondva és kapacitás frissítve.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok("Felhasználó törölve.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/parking-spots/{id}/features")
    public ResponseEntity<?> updateFeatures(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String features = body.get("features");
            return ResponseEntity.ok(adminService.updateParkingSpotFeatures(id, features));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/parking-spots")
    public ResponseEntity<List<ParkingSpot>> getAllParkingSpots() {
        return ResponseEntity.ok(adminService.getAllParkings());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String roleName = body.get("role");
            adminService.updateUserRole(id, roleName);
            return ResponseEntity.ok("Jogosultság sikeresen módosítva.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/zones/{id}/features")
    public ResponseEntity<?> updateZoneFeatures(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zóna nem található ezzel az azonosítóval: " + id));

        zone.setFeatures(body.get("features"));
        zoneRepository.save(zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/zones")
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PostMapping("/zones")
    public ResponseEntity<ZoneDto> createZone(@RequestBody ZoneDto zoneDto) {
        return ResponseEntity.ok(zoneService.createZone(zoneDto));
    }

    @PostMapping("/parking-spots")
    public ResponseEntity<?> createParkingSpot(@RequestBody ParkingSpot parkingSpot) {
        try {

            ParkingSpot savedSpot = parkingSpotRepository.save(parkingSpot);
            return ResponseEntity.ok(savedSpot);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Hiba a parkoló mentésekor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/zones/{id}")
    public ResponseEntity<String> deleteZone(@PathVariable Integer id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok("Zóna törölve!");
    }
}