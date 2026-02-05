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

        // 1. ProductData létrehozása
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

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/success") // Angular frontend URL
                .setCancelUrl("http://localhost:4200/cancel")   // Angular frontend URL
                .addLineItem(lineItem)
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
}