package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.PaymentRepository;
import com.vizsgaremek.backend.repository.UserRepository;
import com.vizsgaremek.backend.mapper.BookingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void testStartOnDemandParking_Success() {
        ParkingSpot spot = new ParkingSpot();
        spot.setId(1);
        spot.setHourlyRate(500);
        when(parkingSpotRepository.findById(1)).thenReturn(Optional.of(spot));

        BookingDto dto = new BookingDto();
        dto.setParkingSpotId(1);
        dto.setLicensePlate("ABC-123");

        when(bookingRepository.save(any())).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            b.setId(1);
            return b;
        });

        Booking result = bookingService.startOnDemandParking(dto);

        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("ABC-123", result.getLicensePlate());
        assertEquals("ON_DEMAND", result.getParkingType());
    }

    @Test
    void testStartOnDemandParking_SpotNotFound() {
        when(parkingSpotRepository.findById(999)).thenReturn(Optional.empty());

        BookingDto dto = new BookingDto();
        dto.setParkingSpotId(999);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookingService.startOnDemandParking(dto);
        });

        assertTrue(ex.getMessage().contains("nem található"));
    }

    @Test
    void testStopOnDemandParking_NotInProgress() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus("COMPLETED");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookingService.stopOnDemandParking(1);
        });

        assertTrue(ex.getMessage().contains("nem lehet leállítani"));
    }

    @Test
    void testStopOnDemandParking_CalculatesPrice() {
        ParkingSpot spot = new ParkingSpot();
        spot.setHourlyRate(600);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus("IN_PROGRESS");
        booking.setCheckInTime(Instant.now().minus(2, ChronoUnit.HOURS));
        booking.setParkingSpot(spot);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.stopOnDemandParking(1);

        assertEquals("PENDING_PAYMENT", result.getStatus());
        assertTrue(result.getTotalPrice() > 0);
        assertNotNull(result.getCheckOutTime());
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus("CANCELLED");
        User user = new User();
        user.setUsername("testuser");
        booking.setUser(user);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(1);
        });

        assertTrue(ex.getMessage().contains("már"));
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            bookingService.getBookingById(999);
        });
    }

    @Test
    void testCancelBooking_Success() {
        Booking booking = new Booking();
        booking.setId(2);
        booking.setStatus("ACTIVE");
        User user = new User();
        user.setUsername("jogosult_user");
        booking.setUser(user);

        when(bookingRepository.findById(2)).thenReturn(Optional.of(booking));

        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("jogosult_user");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.cancelBooking(2);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }
    @Test
    void testCancelBooking_UnauthorizedUser() {
        Booking booking = new Booking();
        booking.setId(3);
        booking.setStatus("ACTIVE");
        User owner = new User();
        owner.setUsername("eredeti_user");
        booking.setUser(owner);

        when(bookingRepository.findById(3)).thenReturn(Optional.of(booking));

        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("hacker_bela");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(3);
        });

        assertNotNull(ex.getMessage());
    }

    @Test
    void testStopOnDemandParking_AlreadyCompleted() {
        Booking booking = new Booking();
        booking.setId(4);
        booking.setStatus("COMPLETED");

        when(bookingRepository.findById(4)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookingService.stopOnDemandParking(4);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("nem lehet"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testGetBookingById_Success() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus("ACTIVE");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }
}