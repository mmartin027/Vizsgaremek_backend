package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService; // <-- REPOSITORY HELYETT A SERVICE-T HASZNÁLJUK!

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Foglalás lemondása Admin által
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer id) {
        try {
            adminService.adminCancelBooking(id);
            return ResponseEntity.ok("Foglalás lemondva és kapacitás frissítve.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}