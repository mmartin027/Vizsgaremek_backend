package com.vizsgaremek.backend.controler;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.DTO.BookingRequest;
import com.vizsgaremek.backend.DTO.StripeResponse;
import com.vizsgaremek.backend.service.BookingService;
import com.vizsgaremek.backend.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/booking") // Módosítva: /api/booking
@CrossOrigin(origins = "http://localhost:4200")
public class BookingCheckoutController {

    private final StripeService stripeService;
    private BookingService bookingService;


    public BookingCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutBooking(@RequestBody BookingRequest bookingRequest) {

        StripeResponse stripeResponse = stripeService.checkoutBooking(bookingRequest); // Javítva

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }


    @PostMapping("/confirm-booking")
    public ResponseEntity<?> confirmBooking(@RequestParam String sessionId) {
        try {
            Session session = stripeService.verifySession(sessionId);

            if (!"complete".equals(session.getStatus()) || !"paid".equals(session.getPaymentStatus())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of("error", "Fizetés nem lett teljesítve"));
            }

            // 3. Metadata kiolvasása (foglalási adatok)
            Map<String, String> metadata = session.getMetadata();

            // 4. BookingDto összeállítása
            BookingDto bookingDto = new BookingDto();
            bookingDto.setUserId(Long.valueOf(Integer.valueOf(metadata.get("userId"))));
            bookingDto.setStartTime(Instant.parse(metadata.get("startTime")));
            bookingDto.setEndTime(Instant.parse(metadata.get("endTime")));
            bookingDto.setLicensePlate(metadata.get("licensePlate"));
            bookingDto.setCarBrand(metadata.get("carBrand"));
            bookingDto.setCarModel(metadata.get("carModel"));
            bookingDto.setCarColor(metadata.get("carColor"));

            // 5. Foglalás létrehozása az adatbázisban
            Integer parkingSpotId = Integer.valueOf(metadata.get("parkingSpotId"));
            String accessCode = bookingService.createBooking(parkingSpotId, bookingDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "accessCode", accessCode,
                    "message", "Foglalás sikeresen létrehozva"
            ));

        } catch (StripeException e) {
            System.err.println("Stripe hiba: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Stripe hiba: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Foglalás létrehozási hiba: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Foglalás létrehozási hiba: " + e.getMessage()));
        }
    }

}