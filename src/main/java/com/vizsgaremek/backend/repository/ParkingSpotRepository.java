package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {
}