package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.UserDto;
import com.vizsgaremek.backend.model.*;
import com.vizsgaremek.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;


    public ParkingSpot addParkingSpot(ParkingSpot spot) {
        return parkingSpotRepository.save(spot);
    }

    @Transactional
    public void deleteZone(Integer id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("A zóna nem található ezzel az ID-val: " + id);
        }
        List<ParkingSpot> spots = parkingSpotRepository.findByZoneId(id);
        for (ParkingSpot spot : spots) {
            parkingSpotRepository.delete(spot);
        }
        zoneRepository.deleteById(id);
    }


    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);
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


    public ParkingSpot updateParkingSpotFeatures(Integer spotId, String features) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Parkoló nem található!"));
        spot.setFeatures(features);
        return parkingSpotRepository.save(spot);
    }

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

    @Transactional
    public void updateUserRole(Integer userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));


        String exactRoleName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;


        Role role = roleRepository.findByName(exactRoleName)
                .orElseThrow(() -> new RuntimeException("A " + exactRoleName + " szerepkör nem létezik az adatbázisban!"));

        user.getRoles().clear();
        user.getRoles().add(role);

        userRepository.save(user);
    }

    public Page<Booking> getAllBookings(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return bookingRepository.findAll(pageable);
    }
    public List<ParkingSpot> getAllParkings(){
        return parkingSpotRepository.findAll();
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    String roleName = "USER";

                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        roleName = user.getRoles().iterator().next().getName();

                        if (roleName.startsWith("ROLE_")) {
                            roleName = roleName.substring(5);
                        }
                    }

                    return new UserDto(
                            user.getId() != null ? user.getId().longValue() : null, // Biztonságos konvertálás Long-ra
                            user.getUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPhone(),
                            user.getProvider(),
                            user.getCreatedAt(),
                            user.getLastLogin(),
                            user.getIsDeleted(),
                            roleName
                    );
                })
                .collect(Collectors.toList());
    }


}