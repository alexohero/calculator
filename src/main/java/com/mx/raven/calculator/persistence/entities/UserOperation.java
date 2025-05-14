package com.mx.raven.calculator.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "raven_operations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String operation;

    @Column(scale = 2)
    private BigDecimal operandA;

    @Column(scale = 2)
    private BigDecimal operandB;

    @Column(scale = 2)
    private BigDecimal result;

    @Column(length = 100)
    private LocalDateTime timestamp;

}
