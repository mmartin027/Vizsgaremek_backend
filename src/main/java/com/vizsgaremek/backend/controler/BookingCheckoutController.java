package com.vizsgaremek.backend.controler;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.Payment;
import com.vizsgaremek.backend.repository.PaymentRepository;
import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.service.BookingService;
import com.vizsgaremek.backend.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
public class BookingCheckoutController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentRepository paymentRepository;

    // BIZTONSÁGOS BEOLVASÁS: A kulcs a Linux szerver környezeti változóiból jön!
    @Value("${stripe.webhook}")
    private String endpointSecret;

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody BookingDto dto, @RequestParam Integer parkingSpotId) {
        try {
            return ResponseEntity.ok(stripeService.createCheckoutSession(dto, parkingSpotId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/create-extension-session")
    public ResponseEntity<?> createExtension(@RequestParam Long bookingId, @RequestParam Integer additionalMinutes) {
        try {
            return ResponseEntity.ok(stripeService.createExtensionSession(bookingId, additionalMinutes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(jakarta.servlet.http.HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // Nyers payload biztonságos beolvasása byte-onként
            String payload = new String(request.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            // Aláírás validálása a titkos kulccsal
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {

                Session eventSession = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                Session session = Session.retrieve(eventSession.getId());

                String stripePaymentId = session.getPaymentIntent();

                // Duplikáció elleni védelem
                if (stripePaymentId != null && paymentRepository.existsByTransactionId(stripePaymentId)) {
                    System.out.println("Webhook: Ezt a fizetést már feldolgoztuk. (Duplikált Webhook eldobva: " + stripePaymentId + ")");
                    return ResponseEntity.ok("");
                }

                Map<String, String> metadata = session.getMetadata();

                if (metadata == null || metadata.isEmpty()) {
                    System.out.println("Webhook Figyelem: A metadata üres!");
                    return ResponseEntity.ok("");
                }

                String type = metadata.get("type");

                // Fizetés típusának megfelelő feldolgozás
                if ("booking".equals(type)) {
                    confirmBookingPayment(metadata, stripePaymentId);
                    System.out.println("Webhook: Új parkolás sikeresen elmentve!");
                } else if ("extension".equals(type)) {
                    confirmExtensionPayment(metadata);
                    System.out.println("Webhook: Parkolás meghosszabbítása sikeresen elmentve!");
                } else if ("STOP_PARKING".equals(type)) {
                    bookingService.confirmStopParkingPayment(metadata, stripePaymentId);
                    System.out.println("Webhook: On-Demand parkolás sikeresen lezárva!");
                }
            }
            return ResponseEntity.ok("");

        } catch (SignatureVerificationException e) {
            System.err.println("Webhook Hiba: Hibás aláírás! A titkosító ellenőrzés elbukott.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibás aláírás");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(""); // Párhuzamos mentés esetén nincs teendő
        } catch (Exception e) {
            System.err.println("Webhook Mentési Hiba Történt!");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String sessionId) {
        try {
            Map<String, String> metadata = stripeService.retrieveSession(sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Fizetés megerősítve a Stripe rendszere alapján.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/stop-session")
    public ResponseEntity<?> stopAndPay(@RequestParam Integer bookingId) {
        try {
            bookingService.stopOnDemandParking(bookingId);

            // Generálunk egy Stripe URL-t a kiszámolt összegre
            Map<String, String> response = stripeService.createStopSession(bookingId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    private void confirmBookingPayment(Map<String, String> metadata, String stripePaymentId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setLicensePlate(metadata.get("licensePlate"));
        bookingDto.setCarBrand(metadata.get("carBrand"));
        bookingDto.setCarModel(metadata.get("carModel"));
        bookingDto.setCarColor(metadata.get("carColor"));
        bookingDto.setStartTime(Instant.parse(metadata.get("startTime")));
        bookingDto.setEndTime(Instant.parse(metadata.get("endTime")));

        if (metadata.get("userId") != null && !metadata.get("userId").isEmpty()) {
            bookingDto.setUserId(Long.parseLong(metadata.get("userId")));
        }

        Integer parkingSpotId = Integer.parseInt(metadata.get("parkingSpotId"));
        bookingService.createBooking(parkingSpotId, bookingDto, stripePaymentId);
    }

    private void confirmExtensionPayment(Map<String, String> metadata) {
        Long bookingId = Long.parseLong(metadata.get("bookingId"));
        Integer additionalMinutes = Integer.parseInt(metadata.get("additionalMinutes"));
        bookingService.extendBooking(bookingId, additionalMinutes);
    }
}