package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.parkingSpot.id = :spotId AND b.status = 'ACTIVE' AND CURRENT_TIMESTAMP BETWEEN b.startTime AND b.endTime")
    long countActiveBookings(@Param("spotId") Integer spotId);

    @Query("SELECT b.parkingSpot.id, COUNT(b) FROM Booking b WHERE b.status = 'ACTIVE' AND CURRENT_TIMESTAMP BETWEEN b.startTime AND b.endTime GROUP BY b.parkingSpot.id")
    List<Object[]> countActiveBookingsPerSpot();

    List<Booking> findByStatus(Enum status);
    Optional<Booking> findByAccessCode(String accessCode);

    @Override
    @EntityGraph(attributePaths = {
            "user",
            "parkingSpot",
            "parkingSpot.city",
            "parkingSpot.zone"
    })
    Page<Booking> findAll(Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.parkingSpot.id = :spotId " +
            "AND b.status != 'CANCELLED' " +
            "AND ((b.startTime < :end AND b.endTime > :start))")
    List<Booking> findActiveBookingsInTimeRange(
            @Param("spotId") Integer spotId,
            @Param("start") Instant startTime,
            @Param("end") Instant endTime
    );
}