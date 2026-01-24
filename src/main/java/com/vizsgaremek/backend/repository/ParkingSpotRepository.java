package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    /**
     * Aktív parkolók lekérdezése város ID alapján
     */
    List<ParkingSpot> findByCityIdAndIsActiveTrue(Integer cityId);


    List<ParkingSpot> findByCity_NameContainingIgnoreCaseAndIsActiveTrue(String cityName);


    Optional<ParkingSpot> findByIdAndIsActiveTrue(Integer id);


    Optional<ParkingSpot> findByNameIgnoreCase(String name);


    List<ParkingSpot> findByAddressContainingIgnoreCaseAndIsActiveTrue(String address);


    List<ParkingSpot> findByTypesContainingIgnoreCaseAndIsActiveTrue(String type);

    /**
     * ParkingSpot-ok óradíj tartomány szerint
     */
    @Query("SELECT p FROM ParkingSpot p WHERE p.hourlyRate BETWEEN :minRate AND :maxRate AND p.isActive = true")
    List<ParkingSpot> findByHourlyRateBetween(
            @Param("minRate") Integer minRate,
            @Param("maxRate") Integer maxRate
    );


    List<ParkingSpot> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer minCapacity);


    @Query("SELECT p FROM ParkingSpot p WHERE p.occupiedSpaces < p.capacity AND p.isActive = true")
    List<ParkingSpot> findAvailableParkingSpots();
}