package com.example.ipr.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransferResponse {

    private boolean canProceed;
    private boolean requiresFee;
    private BigDecimal feeAmount;
}
