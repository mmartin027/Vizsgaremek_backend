package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.UserDto;
import com.vizsgaremek.backend.DTO.ZoneDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.service.AdminService;
import com.vizsgaremek.backend.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")

public class AdminController {

    @Autowired
    private AdminService adminService;

    private final ZoneService zoneService;   // BE KELL INJEKTÁLNI EZT IS!

    public AdminController(ZoneService zoneService,AdminService adminService) {
        this.zoneService = zoneService;
        this.adminService = adminService;
    }

    // Új parkolóhely hozzáadása
    @PostMapping("/parking-spots")
    public ResponseEntity<ParkingSpot> createParkingSpot(@RequestBody ParkingSpot parkingSpot) {
        if (parkingSpot.getOccupiedSpaces() == null) parkingSpot.setOccupiedSpaces(0);
        return ResponseEntity.ok(adminService.addParkingSpot(parkingSpot));
    }

    //Parkolóhely törlése
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

    // Összes foglalás lekérése az Admin Panelhez
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
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


    @GetMapping("/parking-spots")
    public ResponseEntity<List<ParkingSpot>> getAllParkingSpots() {
        return ResponseEntity.ok(adminService.getAllParkings());
    }

    // Összes felhasználó lekérése az Adminnak
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/zones")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PostMapping("/zones")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ZoneDto> createZone(@RequestBody ZoneDto zoneDto) {
        return ResponseEntity.ok(zoneService.createZone(zoneDto));
    }

    @DeleteMapping("/zones/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteZone(@PathVariable Integer id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok("Zóna törölve!");
    }
}

