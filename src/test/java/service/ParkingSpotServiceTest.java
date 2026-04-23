package service;

import com.vizsgaremek.backend.DTO.ParkingSpotDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAvailableSpacesCalculation_ShouldReturnCorrectNumber() {
        ParkingSpot mockSpot = new ParkingSpot();
        mockSpot.setId(1);
        mockSpot.setName("Teszt Garázs");
        mockSpot.setCapacity(50);

        when(parkingSpotRepository.findById(1)).thenReturn(Optional.of(mockSpot));

        when(bookingRepository.countActiveBookings(1)).thenReturn(15L);

        ParkingSpotDto resultDto = parkingSpotService.getByIdentifier("1");

        assertEquals(50, resultDto.getCapacity(), "A kapacitásnak 50-nek kell lennie");
        assertEquals(15, resultDto.getOccupiedSpaces(), "A foglalt helyeknek 15-nek kell lennie");
        assertEquals(35, resultDto.getAvailableSpaces(), "A szabad helyeknek 35-nek kell lennie!");
    }
}