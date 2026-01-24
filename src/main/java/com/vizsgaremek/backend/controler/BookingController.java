package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.service.BookingService;
import com.vizsgaremek.backend.service.ParkingSpotService;
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
    private ParkingSpotService parkingSpotService;

    /**
     * Összes foglalás lekérdezése
     */
    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }

    /**
     * Foglalás lekérdezése ID alapján
     */
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

    /**
     * Foglalás lekérdezése megerősítő kód alapján
     */
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            Booking booking = bookingService.findByConfirmationCode(confirmationCode);
            BookingDto bookingDto = convertToDto(booking);
            return ResponseEntity.ok(bookingDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Foglalás nem található megerősítő kóddal: " + confirmationCode);
        }
    }

    /**
     * Új foglalás létrehozása
     */
    @PostMapping("/parkingspot/{parkingSpotId}")
    public ResponseEntity<?> createBooking(
            @PathVariable Integer parkingSpotId,
            @RequestBody BookingDto bookingDto) {
        try {
            String confirmationCode = bookingService.createBooking(parkingSpotId, bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Foglalás sikerült! Megerősítő kód: " + confirmationCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Foglalás sikertelen: " + e.getMessage());
        }
    }

    /**
     * Foglalás törlése (lemondása)
     */
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

    /**
     * User foglalásainak lekérdezése
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable Integer userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }

    /**
     * Parkolóhely foglalásainak lekérdezése
     */
    @GetMapping("/parkingspot/{parkingSpotId}")
    public ResponseEntity<List<BookingDto>> getBookingsByParkingSpotId(@PathVariable Integer parkingSpotId) {
        List<Booking> bookings = bookingService.getBookingsByParkingSpotId(parkingSpotId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }

    /**
     * Foglalások státusz szerint
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable Enum status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDtos);
    }


    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());

        if (booking.getParkingSpot() != null) {
            dto.setParkingSpotId(booking.getParkingSpot().getId());
            dto.setParkingSpotName(booking.getParkingSpot().getName());
            dto.setParkingSpotAddress(booking.getParkingSpot().getAddress());
        }

        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
            dto.setUserName(booking.getUser().getUsername());
            dto.setUserEmail(booking.getUser().getEmail());
        }

        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setHours(booking.getHours());
        dto.setTotalPrice(booking.getTotalPrice());

        // Autó adatok
        dto.setLicensePlate(booking.getLicensePlate());
        dto.setCarBrand(booking.getCarBrand());
        dto.setCarModel(booking.getCarModel());
        dto.setCarColor(booking.getCarColor());

        // Státusz és extra
        dto.setStatus(booking.getStatus());
        dto.setQrCode(booking.getQrCode());
        dto.setAccessCode(booking.getAccessCode());
        dto.setIsExtended(booking.getIsExtended());

        // Időbélyegek
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setCancelledAt(booking.getCancelledAt());

        return dto;
    }
}