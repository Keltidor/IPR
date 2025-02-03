package com.example.ipr.domain;

import jakarta.validation.constraints.DecimalMin;
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
public class TransferRequestDto {

    @NotNull
    private UUID senderUserId;

    @NotNull
    private UUID senderAccountId;

    @NotNull
    private UUID receiverAccountId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
