package com.vizsgaremek.backend.repository;

import com.vizsgaremek.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
 public interface PaymentRepository extends JpaRepository<Payment, Integer> {

 boolean existsByTransactionId(String transactionId);
}