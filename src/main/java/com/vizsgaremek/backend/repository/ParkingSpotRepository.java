package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    List<ParkingSpot> findByCityIdAndIsActiveTrue(Integer cityId);

    List<ParkingSpot> findByCity_NameContainingIgnoreCaseAndIsActiveTrue(String cityName);

}