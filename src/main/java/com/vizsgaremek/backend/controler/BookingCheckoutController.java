package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.BookingRequest;
import com.vizsgaremek.backend.DTO.StripeResponse;
import com.vizsgaremek.backend.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking") // Módosítva: /api/booking
@CrossOrigin(origins = "http://localhost:4200")
public class BookingCheckoutController {

    private final StripeService stripeService;

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
}