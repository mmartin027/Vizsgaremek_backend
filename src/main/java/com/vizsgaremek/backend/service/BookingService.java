package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.mapper.BookingMapper;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
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
    private final BookingMapper bookingMapper;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található ID-val: " + id));
    }

    public Booking findByaccessCode(String accessCode) {
        return bookingRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található access kóddal: " + accessCode));
    }

    public ParkingSpot getParkingSpot(Integer id) {
        return parkingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parkolóhely nem található ID-val: " + id));
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

        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new RuntimeException("Parkoló nem található ID-val: " + parkingSpotId));

        if (!isParkingSpotAvailable(parkingSpot, bookingDto.getStartTime(), bookingDto.getEndTime())) {
            throw new RuntimeException("A parkoló megtelt ebben az időintervallumban!");
        }



        User user = null;
        if (bookingDto.getUserId() != null) {
            System.out.println(" UserId NEM null, keresés az adatbázisban: " + bookingDto.getUserId());
            user = userRepository.findById(Math.toIntExact(bookingDto.getUserId()))
                    .orElseThrow(() -> new RuntimeException("Felhasználó nem található ID-val: " + bookingDto.getUserId()));
            System.out.println(" User találva: " + user.getUsername() + " (ID: " + user.getId() + ")");
        } else {
            System.out.println("⚠ FIGYELEM: UserId NULL érkezett a BookingDto-ban!");
        }

        long hours = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime()).toHours();
        if (hours == 0) hours = 1;

        Integer totalPrice = calculatePrice(parkingSpot, (int) hours);

        Booking booking = new Booking();
        booking.setParkingSpot(parkingSpot);
        booking.setUser(user);  // Ez NULL lesz, ha userId null volt!
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

        String confirmationCode = generateConfirmationCode();
        booking.setAccessCode(confirmationCode);

        System.out.println("🔍 Booking mentése - User ID: " + (user != null ? user.getId() : "NULL"));
        bookingRepository.save(booking);

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


    public BookingDto extendBooking(Long bookingId, Integer additionalMinutes) {
        Booking booking = bookingRepository.findById(Math.toIntExact(bookingId))
                .orElseThrow(() -> new RuntimeException("Foglalás nem található"));

        booking.setEndTime(booking.getEndTime().plus(Duration.ofMinutes(additionalMinutes)));

        long newHours = Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
        booking.setHours((int) newHours);

        Integer newTotalPrice = calculatePrice(booking.getParkingSpot(), (int) newHours);
        booking.setTotalPrice(newTotalPrice);

        booking.setIsExtended(true);
        booking.setUpdatedAt(Instant.now());

        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toDto(updated);  // ← Most már működik!
    }


    private boolean isParkingSpotAvailable(ParkingSpot parkingSpot, Instant startTime, Instant endTime) {
        List<Booking> activeBookings = bookingRepository.findActiveBookingsInTimeRange(
                parkingSpot.getId(), startTime, endTime);

        return activeBookings.size() < parkingSpot.getCapacity();
    }

    private Integer calculatePrice(ParkingSpot parkingSpot, int hours) {
        if (parkingSpot.getHourlyRate() != null) {
            return parkingSpot.getHourlyRate() * hours;
        }
        return 0;
    }

    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}