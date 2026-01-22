package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking>findByUserId(Integer userId);


    List<Booking> findByParkingSpotId(Integer parkingSpotId );

    List<Booking> findByStatus(Integer StatusId);

}