package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.ParkingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {



    List<ParkingSpot> findByCityIdAndIsActiveTrue(Integer cityId);

    List<ParkingSpot> findByCity_NameContainingIgnoreCaseAndIsActiveTrue(String cityName);

    Optional<ParkingSpot> findByIdAndIsActiveTrue(Integer id);

    Optional<ParkingSpot> findByNameIgnoreCase(String name);

    List<ParkingSpot> findByAddressContainingIgnoreCaseAndIsActiveTrue(String address);

    List<ParkingSpot> findByTypesContainingIgnoreCaseAndIsActiveTrue(String type);

    @Query("SELECT p FROM ParkingSpot p WHERE p.hourlyRate BETWEEN :minRate AND :maxRate AND p.isActive = true")
    List<ParkingSpot> findByHourlyRateBetween(
            @Param("minRate") Integer minRate,
            @Param("maxRate") Integer maxRate
    );

    List<ParkingSpot> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer minCapacity);

    @Query("SELECT p FROM ParkingSpot p WHERE p.occupiedSpaces < p.capacity AND p.isActive = true")
    List<ParkingSpot> findAvailableParkingSpots();



    List<ParkingSpot> findByParkingTypeAndIsActiveTrue(ParkingType parkingType);


    List<ParkingSpot> findByCityIdAndParkingTypeAndIsActiveTrue(Integer cityId, ParkingType parkingType);


    Optional<ParkingSpot> findByZoneCodeAndIsActiveTrue(String zoneCode);


    List<ParkingSpot> findByZoneNameContainingIgnoreCaseAndIsActiveTrue(String zoneName);


    @Query("SELECT p FROM ParkingSpot p WHERE p.parkingType = :parkingType " +
            "AND p.occupiedSpaces < p.capacity AND p.isActive = true")
    List<ParkingSpot> findAvailableByParkingType(@Param("parkingType") ParkingType parkingType);


    @Query("SELECT p FROM ParkingSpot p WHERE p.city.id = :cityId " +
            "AND p.parkingType = :parkingType " +
            "AND p.occupiedSpaces < p.capacity AND p.isActive = true")
    List<ParkingSpot> findAvailableByCityAndType(
            @Param("cityId") Integer cityId,
            @Param("parkingType") ParkingType parkingType
    );


    @Query("SELECT p FROM ParkingSpot p WHERE p.city.id = :cityId " +
            "AND p.parkingType = :parkingType " +
            "AND p.hourlyRate BETWEEN :minRate AND :maxRate " +
            "AND p.occupiedSpaces < p.capacity " +
            "AND p.isActive = true")
    List<ParkingSpot> findAvailableByFilters(
            @Param("cityId") Integer cityId,
            @Param("parkingType") ParkingType parkingType,
            @Param("minRate") Integer minRate,
            @Param("maxRate") Integer maxRate
    );


    List<ParkingSpot> findByParkingType(ParkingType parkingType);


    @Query("SELECT p FROM ParkingSpot p WHERE p.city.id = :cityId " +
            "AND p.parkingType = 'OUTDOOR' AND p.isActive = true " +
            "ORDER BY p.zoneName")
    List<ParkingSpot> findOutdoorZonesByCity(@Param("cityId") Integer cityId);
}