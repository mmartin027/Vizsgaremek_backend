package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {


    Optional<Zone> findByZoneCode(String zoneCode);
}