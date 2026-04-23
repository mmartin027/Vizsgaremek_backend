package com.vizsgaremek.backend.repository;


import com.vizsgaremek.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByUserId(Integer userId);
    Optional<Vehicle> findByUserIdAndIsDefaultTrue(Integer userId);
}