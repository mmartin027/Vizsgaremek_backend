package com.vizsgaremek.backend.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final ParkingSpotRepository parkingSpotRepository;
    private final BookingRepository bookingRepository; // ← ÚJ

    public StripeService(
            @Value("${stripe.api.key}") String stripeApiKey,
            ParkingSpotRepository parkingSpotRepository,
            BookingRepository bookingRepository) { // ← ÚJ paraméter
        this.stripeApiKey = stripeApiKey;
        this.parkingSpotRepository = parkingSpotRepository;
        this.bookingRepository = bookingRepository; // ← ÚJ
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Stripe Checkout Session létrehozása FOGLALÁSHOZ
     * Visszaadja a sessionId-t ÉS az URL-t
     */
    public Map<String, String> createCheckoutSession(BookingDto bookingDto, Integer parkingSpotId) {
        try {
            // ParkingSpot lekérése
            ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                    .orElseThrow(() -> new RuntimeException("Parkoló nem található ID-val: " + parkingSpotId));

            // Órák számítása
            long hours = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime()).toHours();
            if (hours == 0) hours = 1; // Minimum 1 óra

            // Ár számítása
            long totalPrice = parkingSpot.getHourlyRate() * hours;

            // Metadata a foglalási adatokkal (később visszakereséshez)
            Map<String, String> metadata = new HashMap<>();
            metadata.put("type", "booking"); // ← ÚJ: Típus jelzés
            metadata.put("parkingSpotId", parkingSpotId.toString());
            metadata.put("licensePlate", bookingDto.getLicensePlate());
            metadata.put("carBrand", bookingDto.getCarBrand());
            metadata.put("carModel", bookingDto.getCarModel());
            metadata.put("carColor", bookingDto.getCarColor());
            metadata.put("startTime", bookingDto.getStartTime().toString());
            metadata.put("endTime", bookingDto.getEndTime().toString());
            if (bookingDto.getUserId() != null) {
                metadata.put("userId", bookingDto.getUserId().toString());
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:4200/payment-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:4200/payment-cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("huf")
                                                    .setUnitAmount(totalPrice * 100) // Fillérben
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Parkolási foglalás - " + parkingSpot.getName())
                                                                    .setDescription(
                                                                            String.format("Rendszám: %s | %s - %s | %d óra | %d Ft/óra",
                                                                                    bookingDto.getLicensePlate(),
                                                                                    bookingDto.getStartTime(),
                                                                                    bookingDto.getEndTime(),
                                                                                    hours,
                                                                                    parkingSpot.getHourlyRate())
                                                                    )
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putAllMetadata(metadata) // Foglalási adatok tárolása
                    .build();

            Session session = Session.create(params);

            System.out.println("✅ Stripe Session létrehozva:");
            System.out.println("   - Session ID: " + session.getId());
            System.out.println("   - Checkout URL: " + session.getUrl());
            System.out.println("   - Összeg: " + totalPrice + " Ft");
            System.out.println("   - Órák: " + hours);

            // Visszaadjuk mind a sessionId-t, mind az URL-t
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());

            return response;

        } catch (Exception e) {
            System.err.println("❌ Stripe session hiba: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Stripe session létrehozása sikertelen: " + e.getMessage());
        }
    }

    /**
     * ✅ ÚJ: Stripe Checkout Session létrehozása HOSSZABBÍTÁSHOZ
     */
    public Map<String, String> createExtensionSession(Long bookingId, Integer additionalMinutes) {
        try {
            // Foglalás lekérése
            Booking booking = bookingRepository.findById(Math.toIntExact(bookingId))
                    .orElseThrow(() -> new RuntimeException("Foglalás nem található ID-val: " + bookingId));

            ParkingSpot parkingSpot = booking.getParkingSpot();

            // Extra óradíj számítása
            int additionalHours = additionalMinutes / 60;
            if (additionalHours == 0) additionalHours = 1; // Minimum 1 óra
            long additionalPrice = parkingSpot.getHourlyRate() * additionalHours;

            // Metadata - FONTOS: type = "extension"
            Map<String, String> metadata = new HashMap<>();
            metadata.put("type", "extension"); // ← KULCSFONTOSSÁGÚ
            metadata.put("bookingId", bookingId.toString());
            metadata.put("additionalMinutes", additionalMinutes.toString());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:4200/extension-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:4200/foglalasaim")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("huf")
                                                    .setUnitAmount(additionalPrice * 100) // Fillérben
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Foglalás hosszabbítása - " + parkingSpot.getName())
                                                                    .setDescription(
                                                                            String.format("Rendszám: %s | +%d óra | %d Ft/óra",
                                                                                    booking.getLicensePlate(),
                                                                                    additionalHours,
                                                                                    parkingSpot.getHourlyRate())
                                                                    )
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putAllMetadata(metadata) // Extension metadata
                    .build();

            Session session = Session.create(params);

            System.out.println("✅ Extension Session létrehozva:");
            System.out.println("   - Session ID: " + session.getId());
            System.out.println("   - Checkout URL: " + session.getUrl());
            System.out.println("   - Booking ID: " + bookingId);
            System.out.println("   - Extra órák: " + additionalHours);
            System.out.println("   - Extra díj: " + additionalPrice + " Ft");

            // Visszaadjuk mind a sessionId-t, mind az URL-t
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());

            return response;

        } catch (Exception e) {
            System.err.println("❌ Extension session hiba: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Extension session létrehozása sikertelen: " + e.getMessage());
        }
    }

    /**
     * Fizetés megerősítése és foglalás adatok lekérése
     */
    public Map<String, String> retrieveSession(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            if (!"paid".equals(session.getPaymentStatus())) {
                throw new RuntimeException("A fizetés nem került véglegesítésre!");
            }

            System.out.println(" Session lekérve:");
            System.out.println("   - Payment status: " + session.getPaymentStatus());
            System.out.println("   - Metadata: " + session.getMetadata());

            return session.getMetadata(); // Foglalási adatok visszaadása

        } catch (Exception e) {
            System.err.println(" Session lekérési hiba: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Stripe session lekérése sikertelen: " + e.getMessage());
        }
    }
}