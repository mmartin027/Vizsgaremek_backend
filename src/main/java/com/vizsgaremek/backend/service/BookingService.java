package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.mapper.BookingMapper;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Payment;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.PaymentRepository;
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
    private final BookingMapper bookingMapper;
    private final PaymentRepository paymentRepository;

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
    public String createBooking(Integer parkingSpotId, BookingDto bookingDto,String stripePaymentId) {
        if (bookingDto.getEndTime().isBefore(bookingDto.getStartTime())) {
            throw new RuntimeException("A kezdő dátum nem lehet később mint a záró dátum!");
        }

        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new RuntimeException("Parkoló nem található."));

        if (!isParkingSpotAvailable(parkingSpot, bookingDto.getStartTime(), bookingDto.getEndTime())) {
            throw new RuntimeException("Sajnos közben betelt a parkoló!");
        }

        User user = null;

        //  Ha a Webhook vagy az Angular küldött explicit userId-t a DTO-ban, azt használjuk!
        if (bookingDto.getUserId() != null) {
            user = userRepository.findById(bookingDto.getUserId().intValue()).orElse(null);
        }
        //  Ha nincs a DTO-ban userId, akkor megnézzük, be van-e jelentkezve valaki (normál API hívás)
        else if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
            String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();

            if (!"anonymousUser".equals(currentUsername)) {
                user = userRepository.findByUsername(currentUsername).orElse(null);
            }
        }

        // 4. Számítások
        long hours = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime()).toHours();
        if (hours == 0) hours = 1;
        Integer totalPrice = calculatePrice(parkingSpot, (int) hours);

        Booking booking = new Booking();
        booking.setParkingSpot(parkingSpot);
        booking.setUser(user);
        booking.setStartTime(bookingDto.getStartTime());
        booking.setEndTime(bookingDto.getEndTime());
        booking.setTotalPrice(totalPrice);
        booking.setLicensePlate(bookingDto.getLicensePlate());
        booking.setStatus("ACTIVE");

  // 1. Előbb mentjük a booking-ot (mindig!)
        String confirmationCode = generateConfirmationCode();
        booking.setAccessCode(confirmationCode);
        bookingRepository.save(booking);

  //  Ha van Stripe fizetés, utána mentjük a payment-et
        if (stripePaymentId != null && !stripePaymentId.isEmpty()) {
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setTransactionId(stripePaymentId);
            payment.setAmount(totalPrice);
            payment.setStatus("SUCCESS");
            payment.setPaymentMethod("stripe");
            payment.setCreatedAt(Instant.now());
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);
        }

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
        return bookingMapper.toDto(updated);
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