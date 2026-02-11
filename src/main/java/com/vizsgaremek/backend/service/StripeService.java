package com.vizsgaremek.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vizsgaremek.backend.DTO.BookingRequest;
import com.vizsgaremek.backend.DTO.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse checkoutBooking(BookingRequest bookingRequest) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(bookingRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(bookingRequest.getCurrency())
                        .setUnitAmount(bookingRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(bookingRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        // Metadata hozzáadása - foglalási adatok tárolása
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/booking/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4200/booking/cancel")
                .addLineItem(lineItem)
                .putMetadata("parkingSpotId", String.valueOf(bookingRequest.getParkingSpotId()))
                .putMetadata("userId", String.valueOf(bookingRequest.getUserId()))
                .putMetadata("startTime", bookingRequest.getStartTime())
                .putMetadata("endTime", bookingRequest.getEndTime())
                .putMetadata("licensePlate", bookingRequest.getLicensePlate())
                .putMetadata("carBrand", bookingRequest.getCarBrand())
                .putMetadata("carModel", bookingRequest.getCarModel())
                .putMetadata("carColor", bookingRequest.getCarColor())
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            System.out.println("Stripe hiba: " + ex.getMessage());
            return StripeResponse.builder()
                    .status("Error")
                    .message("Fizetési hiba: " + ex.getMessage())
                    .build();
        }

        return StripeResponse.builder()
                .status("Success")
                .message("Sikeres fizetés")
                .sessionID(session.getId())
                .sessionURL(session.getUrl())
                .build();
    }

    public Session verifySession(String sessionId) throws StripeException {
        Stripe.apiKey = secretKey;
        return Session.retrieve(sessionId);
    }
}