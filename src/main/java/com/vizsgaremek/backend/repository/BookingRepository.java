package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Booking;
import com.vizsgaremek.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking>findByUserId(Integer userId);


    List<Booking> findByParkingSpotId(Integer parkingSpotId );

    List<Booking> findByStatus(Enum StatusId);

    Optional<Booking> findByAccessCode(String accesCodeId);

    List<Booking> findActiveBookingsInTimeRange(Integer id, Instant startTime, Instant endTime);
}