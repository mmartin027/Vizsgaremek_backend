package com.vizsgaremek.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
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

    private final ParkingSpotRepository parkingSpotRepository;
    private final BookingRepository bookingRepository;

    // 1. Létrehozzuk a frontend URL változót
    private final String frontendUrl;

    public StripeService(
            @Value("${stripe.secretKey}") String stripeApiKey,
            @Value("${app.base-url}") String frontendUrl,
            ParkingSpotRepository parkingSpotRepository,
            BookingRepository bookingRepository) {

        this.parkingSpotRepository = parkingSpotRepository;
        this.bookingRepository = bookingRepository;
        this.frontendUrl = frontendUrl;
        Stripe.apiKey = stripeApiKey;
    }

    public Map<String, String> createCheckoutSession(BookingDto bookingDto, Integer parkingSpotId) {
        try {
            ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                    .orElseThrow(() -> new RuntimeException("Parkoló nem található ID-val: " + parkingSpotId));

            long hours = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime()).toHours();
            if (hours <= 0) hours = 1;
            long totalPrice = parkingSpot.getHourlyRate() * hours;

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(this.frontendUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(this.frontendUrl + "/payment-failed")
                    .putMetadata("type", "booking")
                    .putMetadata("licensePlate", bookingDto.getLicensePlate())
                    .putMetadata("carBrand", bookingDto.getCarBrand())
                    .putMetadata("carModel", bookingDto.getCarModel())
                    .putMetadata("carColor", bookingDto.getCarColor())
                    .putMetadata("startTime", bookingDto.getStartTime().toString())
                    .putMetadata("endTime", bookingDto.getEndTime().toString())
                    .putMetadata("parkingSpotId", parkingSpotId.toString())
                    .putMetadata("userId", bookingDto.getUserId() != null ? bookingDto.getUserId().toString() : "")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("huf")
                                    .setUnitAmount(totalPrice * 100) // Stripe fillérben számol
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Parkoló foglalás - " + parkingSpot.getName())
                                            .build())
                                    .build())
                            .build())
                    .build();

            Session session = Session.create(params);
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Stripe hiba: " + e.getMessage());
        }
    }

    public Map<String, String> createStopSession(Integer bookingId) throws StripeException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található!"));

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Parkolás díja (" + booking.getLicensePlate() + ")")
                        .setDescription("Perc alapú parkolás lezárása.")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("huf")
                        .setUnitAmount((long) booking.getTotalPrice() * 100)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Ugyanaz a javítás itt is!
                .setSuccessUrl(this.frontendUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(this.frontendUrl + "/foglalasaim")
                .addLineItem(lineItem)
                .putMetadata("bookingId", String.valueOf(booking.getId()))
                .putMetadata("type", "STOP_PARKING")
                .build();

        Session session = Session.create(params);

        return Map.of("sessionId", session.getId(), "url", session.getUrl());
    }

    public Map<String, String> createExtensionSession(Long bookingId, Integer additionalMinutes) {
        try {
            Booking booking = bookingRepository.findById(Math.toIntExact(bookingId))
                    .orElseThrow(() -> new RuntimeException("Foglalás nem található."));

            int additionalHours = Math.max(1, additionalMinutes / 60);
            long additionalPrice = booking.getParkingSpot().getHourlyRate() * additionalHours;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    // És itt is!
                    .setSuccessUrl(this.frontendUrl + "/extension-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(this.frontendUrl + "/foglalasaim")
                    .putMetadata("type", "extension")
                    .putMetadata("bookingId", bookingId.toString())
                    .putMetadata("additionalMinutes", additionalMinutes.toString())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("huf")
                                    .setUnitAmount(additionalPrice * 100)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Hosszabbítás - " + booking.getParkingSpot().getName())
                                            .build())
                                    .build())
                            .build())
                    .build();

            Session session = Session.create(params);
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Stripe hiba: " + e.getMessage());
        }
    }

    public Map<String, String> retrieveSession(String sessionId) throws Exception {
        Session session = Session.retrieve(sessionId);
        return session.getMetadata();
    }
}