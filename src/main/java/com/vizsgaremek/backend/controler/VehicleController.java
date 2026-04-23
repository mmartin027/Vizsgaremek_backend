package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.model.Vehicle;
import com.vizsgaremek.backend.repository.UserRepository;
import com.vizsgaremek.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található"));
    }

    @GetMapping
    public List<Vehicle> getMyVehicles() {
        return vehicleRepository.findByUserId(Math.toIntExact(getCurrentUser().getId()));
    }

    @GetMapping("/default")
    public ResponseEntity<?> getDefaultVehicle() {
        User user = getCurrentUser();
        return vehicleRepository.findByUserIdAndIsDefaultTrue(Math.toIntExact(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public Vehicle addVehicle(@RequestBody Map<String, String> body) {
        User user = getCurrentUser();

        String plate = body.getOrDefault("licensePlate", "").toUpperCase().replace("-", "");

        if (!plate.matches("^[A-Z]{3}\\d{3,4}$") && !plate.matches("^[A-Z]{4}\\d{3,4}$")) {
            throw new RuntimeException("Érvénytelen rendszám formátum!");
        }

        if (plate.matches("^[A-Z]{3}\\d{3,4}$")) {
            plate = plate.substring(0, 3) + "-" + plate.substring(3);
        } else {
            plate = plate.substring(0, 4) + "-" + plate.substring(4);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setLicensePlate(plate);
        vehicle.setBrand(body.getOrDefault("brand", ""));
        vehicle.setModel(body.getOrDefault("model", ""));
        vehicle.setColor(body.getOrDefault("color", ""));

        List<Vehicle> existing = vehicleRepository.findByUserId(Math.toIntExact(user.getId()));
        vehicle.setIsDefault(existing.isEmpty());

        return vehicleRepository.save(vehicle);
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefault(@PathVariable Integer id) {
        User user = getCurrentUser();
        List<Vehicle> vehicles = vehicleRepository.findByUserId(Math.toIntExact(user.getId()));

        for (Vehicle v : vehicles) {
            v.setIsDefault(v.getId().equals(id));
            vehicleRepository.save(v);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jármű nem található"));

        if (!vehicle.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Nincs jogosultság");
        }

        vehicleRepository.delete(vehicle);
        return ResponseEntity.ok(Map.of("success", true));
    }
}