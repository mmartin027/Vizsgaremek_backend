package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.DTO.ExtendedBookingDTO;
import com.vizsgaremek.backend.mapper.BookingMapper;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.service.BookingService;
import com.vizsgaremek.backend.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;


        @Autowired
        private BookingMapper bookingMapper;



    @Autowired
    private ParkingSpotService parkingSpotService;


    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        try {
            Booking booking = bookingService.getBookingById(id);
            BookingDto bookingDto = convertToDto(booking);
            return ResponseEntity.ok(bookingDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Foglalás nem található ID-val: " + id);
        }
    }


    @GetMapping("/confirmation/{accessCode}")
    public ResponseEntity<?> getBookingByaccessCode(@PathVariable String accessCode) {
        try {
            Booking booking = bookingService.findByaccessCode(accessCode);
            BookingDto bookingDto = convertToDto(booking);
            return ResponseEntity.ok(bookingDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Foglalás nem található megerősítő kóddal: " + accessCode);
        }
    }


    @PostMapping("/parkingspot/{parkingSpotId}")
    public ResponseEntity<?> createBooking(
            @PathVariable Integer parkingSpotId,
            @RequestBody BookingDto bookingDto) {
        try {
            String accessCode = bookingService.createBooking(parkingSpotId, bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Foglalás sikerült! Megerősítő kód: " + accessCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Foglalás sikertelen: " + e.getMessage());
        }
    }


    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Foglalás sikeresen törölve ID: " + bookingId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Foglalás törlése sikertelen: " + e.getMessage());
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable Integer userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }


    @GetMapping("/parkingspot/{parkingSpotId}")
    public ResponseEntity<List<BookingDto>> getBookingsByParkingSpotId(@PathVariable Integer parkingSpotId) {
        List<Booking> bookings = bookingService.getBookingsByParkingSpotId(parkingSpotId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable Enum status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }


    @PatchMapping("/{bookingId}/extend")
    public ResponseEntity<BookingDto> extendBooking(
            @PathVariable Long bookingId,
        @Valid @RequestBody ExtendedBookingDTO request
    ) {
        BookingDto extended = bookingService.extendBooking(bookingId, request.getAdditionalMinutes());
        return ResponseEntity.ok(extended);
    }

    private BookingDto convertToDto(Booking booking) {
        return bookingMapper.toDto(booking);
    }
}


