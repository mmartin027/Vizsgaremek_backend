package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // --- PARKOLÓHELYEK ---

    public ParkingSpot addParkingSpot(ParkingSpot spot) {
        return parkingSpotRepository.save(spot);
    }

    @Transactional
    public void deleteParkingSpot(Integer id) {
        ParkingSpot spot = parkingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parkoló nem található!"));

        boolean hasActiveBookings = bookingRepository.existsByParkingSpotIdAndStatus(id, "ACTIVE");
        if (hasActiveBookings) {
            throw new RuntimeException("Nem törölhető a parkoló, mert aktív foglalások vannak benne!");
        }

        parkingSpotRepository.delete(spot);
    }

    // --- FOGLALÁSOK ---

    @Transactional
    public void adminCancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Foglalás nem található!"));

        // Kapacitás felszabadítása
        ParkingSpot spot = booking.getParkingSpot();
        if (spot != null) {
            spot.setOccupiedSpaces(Math.max(0, spot.getOccupiedSpaces() - 1));
            parkingSpotRepository.save(spot);
        }

        // Törlés helyett státuszállítás (ajánlott)
        booking.setStatus("CANCELLED_BY_ADMIN");
        bookingRepository.save(booking);
    }
}