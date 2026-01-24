package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található ID-val: " + id));
    }

    public Booking getParkingSpot(Integer id){
        return ParkingSpotRepository.findById(id);

    }



    public Booking findByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByAccessCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található megerősítő kóddal: " + confirmationCode));
    }


    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }


    public List<Booking> getBookingsByParkingSpotId(Integer parkingSpotId) {
        return bookingRepository.findByParkingSpotId(parkingSpotId);
    }


    public List<Booking> getBookingsByStatus(Enum status) {
        return bookingRepository.findByStatus(status);
    }


    @Transactional
    public String createBooking(Integer parkingSpotId, BookingDto bookingDto) {
        // 1. Validáció: időpontok ellenőrzése
        if (bookingDto.getEndTime().isBefore(bookingDto.getStartTime())) {
            throw new RuntimeException("A kezdő dátum nem lehet később mint a záró dátum!");
        }

        // 2. ParkingSpot lekérdezése
        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new RuntimeException("Parkoló nem található ID-val: " + parkingSpotId));

        // 3. Kapacitás ellenőrzése
        if (!isParkingSpotAvailable(parkingSpot, bookingDto.getStartTime(), bookingDto.getEndTime())) {
            throw new RuntimeException("A parkoló megtelt ebben az időintervallumban!");
        }

        // 4. User lekérdezése (ha van userId)
        User user = null;
        if (bookingDto.getUserId() != null) {
            user = userRepository.findById(bookingDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Felhasználó nem található ID-val: " + bookingDto.getUserId()));
        }

        // 5. Órák számítása
        long hours = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime()).toHours();
        if (hours == 0) hours = 1; // Min 1 óra

        // 6. Ár számítása
        Integer totalPrice = calculatePrice(parkingSpot, (int) hours);

        // 7. Booking entity létrehozása
        Booking booking = new Booking();
        booking.setParkingSpot(parkingSpot);
        booking.setUser(user);
        booking.setStartTime(bookingDto.getStartTime());
        booking.setEndTime(bookingDto.getEndTime());
        booking.setHours((int) hours);
        booking.setTotalPrice(totalPrice);
        booking.setLicensePlate(bookingDto.getLicensePlate());
        booking.setCarBrand(bookingDto.getCarBrand());
        booking.setCarModel(bookingDto.getCarModel());
        booking.setCarColor(bookingDto.getCarColor());
        booking.setStatus("ACTIVE");
        booking.setIsExtended(false);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        // 8. Megerősítő kód generálása
        String confirmationCode = generateConfirmationCode();
        booking.setAccessCode(confirmationCode);



        // 10. Mentés
        bookingRepository.save(booking);

        // 11. ParkingSpot occupied_spaces növelése
        parkingSpot.setOccupiedSpaces(parkingSpot.getOccupiedSpaces() + 1);
        parkingSpotRepository.save(parkingSpot);

        return confirmationCode;
    }


    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található ID-val: " + bookingId));

        // Státusz frissítése
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);

        // ParkingSpot occupied_spaces csökkentése
        ParkingSpot parkingSpot = booking.getParkingSpot();
        if (parkingSpot.getOccupiedSpaces() > 0) {
            parkingSpot.setOccupiedSpaces(parkingSpot.getOccupiedSpaces() - 1);
            parkingSpotRepository.save(parkingSpot);
        }
    }

    /**
     * Ellenőrzi, van-e szabad hely a parkolóban az adott időintervallumban
     */
    private boolean isParkingSpotAvailable(ParkingSpot parkingSpot, Instant startTime, Instant endTime) {
        // Lekérdezzük az aktív foglalásokat ebben az időintervallumban
        List<Booking> activeBookings = bookingRepository.findActiveBookingsInTimeRange(
                parkingSpot.getId(), startTime, endTime);

        // Ha az aktív foglalások száma >= kapacitás, akkor nincs hely
        return activeBookings.size() < parkingSpot.getCapacity();
    }

    /**
     * Ár kalkuláció
     */
    private Integer calculatePrice(ParkingSpot parkingSpot, int hours) {
        // Egyszerű óradíj számítás
        if (parkingSpot.getHourlyRate() != null) {
            return parkingSpot.getHourlyRate() * hours;
        }
        return 0;
    }

    /**
     * Megerősítő kód generálása
     */
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(