package com.vizsgaremek.backend.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vizsgaremek.backend.DTO.BookingDto;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stripe Fizetési Szolgáltatás Tesztek")
class StripeServiceTest {

    private StripeService stripeService;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        // Mivel a StripeService konstruktora @Value annotációkat használ,
        // a tesztben manuálisan hozzuk létre kamu ("dummy") adatokkal.
        stripeService = new StripeService(
                "sk_test_dummy_key",
                "http://localhost:4200",
                parkingSpotRepository,
                bookingRepository
        );
    }

    @Test
    @DisplayName("Sikeres Checkout Session Létrehozása")
    void testCreateCheckoutSession_Success() {
        // Arrange
        ParkingSpot spot = new ParkingSpot();
        spot.setId(1);
        spot.setName("Központi Parkoló");
        spot.setHourlyRate(500);

        BookingDto dto = new BookingDto();
        dto.setLicensePlate("ABC-123");
        dto.setStartTime(Instant.now());
        dto.setEndTime(Instant.now().plus(2, ChronoUnit.HOURS)); // 2 óra = 1000 Ft

        when(parkingSpotRepository.findById(1)).thenReturn(Optional.of(spot));

        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {

            Session fakeSession = new Session();
            fakeSession.setId("cs_test_12345");
            fakeSession.setUrl("https://checkout.stripe.com/pay/cs_test_12345");

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(fakeSession);

            Map<String, String> response = stripeService.createCheckoutSession(dto, 1);

            assertNotNull(response);
            assertEquals("cs_test_12345", response.get("sessionId"));
            assertEquals("https://checkout.stripe.com/pay/cs_test_12345", response.get("url"));
        }
    }

    @Test
    @DisplayName("Checkout Hiba: Parkoló nem található")
    void testCreateCheckoutSession_ParkingSpotNotFound() {
        // Arrange
        when(parkingSpotRepository.findById(99)).thenReturn(Optional.empty());

        BookingDto dto = new BookingDto();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            stripeService.createCheckoutSession(dto, 99);
        });

        assertTrue(ex.getMessage().contains("Parkoló nem található ID-val"));
    }

    @Test
    @DisplayName("Sikeres Stop Session Létrehozása (Parkolás befejezése)")
    void testCreateStopSession_Success() throws StripeException {
        // Arrange
        Booking booking = new Booking();
        booking.setId(10);
        booking.setLicensePlate("XYZ-987");
        booking.setTotalPrice(1500);

        when(bookingRepository.findById(10)).thenReturn(Optional.of(booking));

        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {

            Session fakeSession = new Session();
            fakeSession.setId("cs_test_stop_999");
            fakeSession.setUrl("https://checkout.stripe.com/pay/stop");

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(fakeSession);

            Map<String, String> response = stripeService.createStopSession(10);

            assertNotNull(response);
            assertEquals("cs_test_stop_999", response.get("sessionId"));
            assertEquals("https://checkout.stripe.com/pay/stop", response.get("url"));
        }
    }

    @Test
    @DisplayName("Hosszabbítás Hiba: Foglalás nem található")
    void testCreateExtensionSession_BookingNotFound() {
        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            stripeService.createExtensionSession(99L, 60);
        });

        assertTrue(ex.getMessage().contains("Foglalás nem található"));
    }
}