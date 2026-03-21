package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.UserDto;
import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.repository.ParkingSpotRepository;
import com.vizsgaremek.backend.repository.BookingRepository;
import com.vizsgaremek.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;



    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;


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


        ParkingSpot spot = booking.getParkingSpot();
        if (spot != null) {
            spot.setOccupiedSpaces(Math.max(0, spot.getOccupiedSpaces() - 1));
            parkingSpotRepository.save(spot);
        }

        booking.setStatus("CANCELLED_BY_ADMIN");
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<ParkingSpot> getAllParkings(){
        return parkingSpotRepository.findAll();
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone(),
                        user.getProvider(),
                        user.getCreatedAt(),
                        user.getLastLogin(),
                        user.getIsDeleted()
                ))
                .collect(Collectors.toList());
    }


}