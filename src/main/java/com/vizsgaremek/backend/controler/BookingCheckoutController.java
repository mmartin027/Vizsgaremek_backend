package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.BookingDto;
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

    @PostMapping("/create-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestBody BookingDto bookingDto,
            @RequestParam Integer parkingSpotId) {
        try {
            Map<String, String> response = stripeService.createCheckoutSession(bookingDto, parkingSpotId);
            return ResponseEntity.ok(response); // ✅ Most már sessionId ÉS url is visszajön

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    /**
     * 2. LÉPÉS: Fizetés sikeres → Foglalás mentése
     */
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String sessionId) {
        try {
            // 1. Stripe session adatok lekérése
            Map<String, String> metadata = stripeService.retrieveSession(sessionId);

            // 2. BookingDto újraépítése a metadata-ból
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

            // 3. Foglalás mentése az adatbázisba
            String accessCode = bookingService.createBooking(parkingSpotId, bookingDto);

            // 4. Sikeres válasz
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessCode", accessCode);
            response.put("message", "Fizetés sikeres! Foglalás létrehozva.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "error", "Foglalás mentése sikertelen: " + e.getMessage()
                    ));
        }
    }
}