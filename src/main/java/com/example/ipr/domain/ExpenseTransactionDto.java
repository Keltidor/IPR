package com.example.ipr.domain;

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
public class ExpenseTransactionDto {

    private UUID transactionId;
    private BigDecimal amount;
    private Integer currencyId;
    private LocalDateTime transactionDate;
    private String description;
}
