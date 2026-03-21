package com.vizsgaremek.backend.controler;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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
@CrossOrigin(origins = "${app.cors.origins}")
public class BookingCheckoutController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${STRIPE.WEBHOOK.SECRET}")
    private String endpointSecret;

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody BookingDto dto, @RequestParam Integer parkingSpotId) {
        try {
            return ResponseEntity.ok(stripeService.createCheckoutSession(dto, parkingSpotId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 2. EZT HIVJA AZ ANGULAR A HOSSZABBÍTÁSNÁL
     */
    @PostMapping("/create-extension-session")
    public ResponseEntity<?> createExtension(@RequestParam Long bookingId, @RequestParam Integer additionalMinutes) {
        try {
            return ResponseEntity.ok(stripeService.createExtensionSession(bookingId, additionalMinutes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

  //Ezt hívja a Stripe szervere
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        System.out.println(" WEBHOOK HÍVÁS ÉRKEZETT ===");


        try {


            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Esemény típusa: " + event.getType());

            if ("checkout.session.completed".equals(event.getType())) {

                // 1. BIZTONSÁGOS BEOLVASÁS
                Session eventSession = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                Session session = Session.retrieve(eventSession.getId());

                String stripePaymentId = session.getPaymentIntent();

                if (stripePaymentId != null && paymentRepository.existsByTransactionId(stripePaymentId)) {
                    System.out.println(" Ezt a fizetést már feldolgoztuk! (Duplikált Webhook eldobva: " + stripePaymentId + ")");
                    return ResponseEntity.ok(""); // Kilépünk, nem mentjük el mégegyszer!
                }



                Map<String, String> metadata = session.getMetadata();
                System.out.println("Kivont Metadata: " + metadata);

                if (metadata == null || metadata.isEmpty()) {
                    System.out.println(" Figyelem: A metadata üres! ");
                    return ResponseEntity.ok("");
                }

                String type = metadata.get("type");
                System.out.println("Fizetés típusa: " + type);

                if ("booking".equals(type)) {
                    System.out.println("Mentés indítása az adatbázisba (Booking)...");


                    // 2. Átadjuk mindkét paramétert a metódusnak!
                    confirmBookingPayment(metadata, stripePaymentId);

                } else if ("extension".equals(type)) {
                    System.out.println("Hosszabbítás indítása az adatbázisba...");
                    confirmExtensionPayment(metadata);
                }

                System.out.println(" Webhook: Sikeres fizetés feldolgozva és elmentve!");
            }
            return ResponseEntity.ok("");

        } catch (SignatureVerificationException e) {
            System.err.println(" WEBHOOK HIBA: Hibás aláírás! (Lehet, hogy megváltozott a whsec_ kulcs?)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibás aláírás");
        }catch (DataIntegrityViolationException e) {
                return ResponseEntity.ok(""); // Duplikált, de ez nem hiba

        } catch (Exception e) {
            System.err.println(" WEBHOOK MENTÉSI HIBA TÖRTÉNT ");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba: " + e.getMessage());
        }

    }

   //ezt hívja a frontend a sikeres fizetés után
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