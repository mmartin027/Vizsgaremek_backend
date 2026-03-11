package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(Integer userId);

    List<Booking> findByParkingSpotId(Integer parkingSpotId);


    boolean existsByParkingSpotIdAndStatus(Integer parkingSpotId, String status);

    List<Booking> findByStatus(Enum status);

    Optional<Booking> findByAccessCode(String accessCode);

    @Query("SELECT b FROM Booking b WHERE b.parkingSpot.id = :spotId " +
            "AND b.status != 'CANCELLED' " +
            "AND ((b.startTime < :end AND b.endTime > :start))")
    List<Booking> findActiveBookingsInTimeRange(
            @Param("spotId") Integer spotId,
            @Param("start") Instant startTime,
            @Param("end") Instant endTime
    );
}