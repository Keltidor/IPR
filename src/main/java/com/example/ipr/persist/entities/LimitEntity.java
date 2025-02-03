package com.example.ipr.persist.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "limits", schema = "ipr")
public class LimitEntity extends BaseUuidEntity {

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;

    @Column(name = "currency_id", nullable = false)
    private Integer currencyId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
