package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")


public class BookingController {



    private final BookingService bookingService;

    /**
     * Új foglalás létrehozása
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse booking = bookingService.createBooking(request);
        return  ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }


}
