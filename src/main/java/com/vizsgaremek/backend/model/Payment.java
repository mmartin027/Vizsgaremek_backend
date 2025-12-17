package com.vizsgaremek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @ColumnDefault("'pending'")

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";


    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    @Column(name = "card_type", length = 20)
    private String cardType;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}