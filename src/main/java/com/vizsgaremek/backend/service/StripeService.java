package com.vizsgaremek.backend.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vizsgaremek.backend.DTO.BookingDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public StripeService(@Value("${stripe.api.key}") String stripeApiKey) {
        this.stripeApiKey = stripeApiKey;
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Stripe Checkout Session létrehozása
     */
    public String createCheckoutSession(BookingDto bookingDto, Integer parkingSpotId) {
        try {
            // Metadata a foglalási adatokkal (később visszakereséshez)
            Map<String, String> metadata = new HashMap<>();
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
                                                    .setUnitAmount((long) bookingDto.getTotalPrice() * 100) // Fillérben
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Parkolási foglalás")
                                                                    .setDescription(
                                                                            String.format("Rendszám: %s | %s - %s",
                                                                                    bookingDto.getLicensePlate(),
                                                                                    bookingDto.getStartTime(),
                                                                                    bookingDto.getEndTime())
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
            return session.getId(); // Session ID visszaküldése

        } catch (Exception e) {
            throw new RuntimeException("Stripe session létrehozása sikertelen: " + e.getMessage());
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

            return session.getMetadata(); // Foglalási adatok visszaadása

        } catch (Exception e) {
            throw new RuntimeException("Stripe session lekérése sikertelen: " + e.getMessage());
        }
    }
}