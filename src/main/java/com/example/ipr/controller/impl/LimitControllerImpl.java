package com.example.ipr.controller.impl;

import com.example.ipr.controller.LimitController;
import com.example.ipr.domain.CheckTransferDto;
import com.example.ipr.domain.CheckTransferResponse;
import com.example.ipr.domain.SetLimitDto;
import com.example.ipr.persist.entities.LimitEntity;
import com.example.ipr.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/limit")
public class LimitControllerImpl implements LimitController {

    private final LimitService limitService;

    @Override
    public ResponseEntity<LimitEntity> setLimit(SetLimitDto setLimitDto) {
        LimitEntity limit = limitService.setLimit(
                setLimitDto.getAccountId(),
                setLimitDto.getLimitAmount(),
                setLimitDto.getCurrencyId()
        );
        return ResponseEntity.ok(limit);
    }

    @Override
    public ResponseEntity<CheckTransferResponse> checkTransfer(CheckTransferDto request) {
        CheckTransferResponse response = limitService.checkTransferLimit(
                request.getAccountId(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }
}
