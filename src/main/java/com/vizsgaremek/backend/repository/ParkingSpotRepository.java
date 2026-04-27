package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ParkingSpot;
import com.vizsgaremek.backend.model.ParkingType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ParkingSpot p WHERE p.id = :id AND p.isActive = true")
    Optional<ParkingSpot> findByIdWithLock(@Param("id") Integer id);

    Optional<ParkingSpot> findByUuid(String uuid);

    @Override
    @EntityGraph(attributePaths = {"city", "zone"})
    List<ParkingSpot> findAll();

    Optional<ParkingSpot> findFirstByZoneId(Integer zoneId);

    List<ParkingSpot> findByZoneId(Integer zoneId);

    List<ParkingSpot> findByCityIdAndIsActiveTrue(Integer cityId);

    List<ParkingSpot> findByCityNameAndIsActiveTrue(String cityName);


    List<ParkingSpot> findByCity_NameContainingIgnoreCaseAndIsActiveTrue(String cityName);

    Optional<ParkingSpot> findByIdAndIsActiveTrue(Integer id);

    List<ParkingSpot> findByZone_IdAndIsActiveTrue(Integer zoneId);


    Optional<ParkingSpot> findByZone_ZoneCodeAndIsActiveTrue(String zoneCode);

    List<ParkingSpot> findByZone_NameContainingIgnoreCaseAndIsActiveTrue(String zoneName);

    Optional<ParkingSpot> findByNameIgnoreCase(String name);

    List<ParkingSpot> findByAddressContainingIgnoreCaseAndIsActiveTrue(String address);


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

    @Query("SELECT ps FROM ParkingSpot ps LEFT JOIN FETCH ps.zone LEFT JOIN FETCH ps.city WHERE ps.city.id = :cityId AND ps.isActive = true")
    List<ParkingSpot> findByCityIdWithZoneAndCity(@Param("cityId") Integer cityId);


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
            "ORDER BY p.zone.id")
    List<ParkingSpot> findOutdoorZonesByCity(@Param("cityId") Integer cityId);
}