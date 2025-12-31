package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
}