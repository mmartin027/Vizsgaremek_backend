package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.DTO.ExtendedBookingDTO;
import com.vizsgaremek.backend.service.BookingService;
import com.vizsgaremek.backend.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingCheckoutController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private BookingService bookingService;

    /**
     * Stripe Session létrehozása FOGLALÁSHOZ
     */
    @PostMapping("/create-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestBody BookingDto bookingDto,
            @RequestParam Integer parkingSpotId) {
        try {
            Map<String, String> response = stripeService.createCheckoutSession(bookingDto, parkingSpotId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     *  ÚJ: Stripe Session létrehozása HOSSZABBÍTÁSHOZ
     */
    @PostMapping("/create-extension-session")
    public ResponseEntity<?> createExtensionSession(
            @RequestParam Long bookingId,
            @RequestParam Integer additionalMinutes) {
        try {
            Map<String, String> response = stripeService.createExtensionSession(bookingId, additionalMinutes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Fizetés megerősítése FOGLALÁSHOZ
     */
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String sessionId) {
        try {
            Map<String, String> metadata = stripeService.retrieveSession(sessionId);

            String type = metadata.get("type");

            if ("booking".equals(type)) {
                // Normál foglalás mentése
                return confirmBookingPayment(metadata);
            } else if ("extension".equals(type)) {
                // Hosszabbítás mentése
                return confirmExtensionPayment(metadata);
            } else {
                throw new RuntimeException("Ismeretlen fizetési típus: " + type);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Foglalás fizetés megerősítése és mentése
     */
    private ResponseEntity<?> confirmBookingPayment(Map<String, String> metadata) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setLicensePlate(metadata.get("licensePlate"));
        bookingDto.setCarBrand(metadata.get("carBrand"));
        bookingDto.setCarModel(metadata.get("carModel"));
        bookingDto.setCarColor(metadata.get("carColor"));
        bookingDto.setStartTime(Instant.parse(metadata.get("startTime")));
        bookingDto.setEndTime(Instant.parse(metadata.get("endTime")));

        if (metadata.containsKey("userId")) {
            bookingDto.setUserId(Long.parseLong(metadata.get("userId")));
        }

        Integer parkingSpotId = Integer.parseInt(metadata.get("parkingSpotId"));
        String accessCode = bookingService.createBooking(parkingSpotId, bookingDto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("accessCode", accessCode);
        response.put("message", "Fizetés sikeres! Foglalás létrehozva.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * : Hosszabbítás fizetés megerősítése és mentése
     */
    private ResponseEntity<?> confirmExtensionPayment(Map<String, String> metadata) {
        Long bookingId = Long.parseLong(metadata.get("bookingId"));
        Integer additionalMinutes = Integer.parseInt(metadata.get("additionalMinutes"));

        // Foglalás hosszabbítása az adatbázisban
        BookingDto updatedBooking = bookingService.extendBooking(bookingId, additionalMinutes);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("booking", updatedBooking);
        response.put("message", "Fizetés sikeres! Foglalás hosszabbítva.");

        return ResponseEntity.ok(response);
    }
}