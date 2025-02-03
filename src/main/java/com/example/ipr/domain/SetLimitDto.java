package com.example.ipr.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetLimitDto {

    @NotNull
    private UUID accountId;

    @NotNull
    private BigDecimal limitAmount;

    @NotNull
    private Integer currencyId;
}
