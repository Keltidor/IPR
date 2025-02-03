package com.example.ipr.controller.impl;

import com.example.ipr.controller.AccountController;
import com.example.ipr.domain.CreateAccountDto;
import com.example.ipr.persist.entities.AccountEntity;
import com.example.ipr.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    public ResponseEntity<AccountEntity> createAccount(CreateAccountDto createAccountDto) {
        AccountEntity account = accountService.createAccount(
                createAccountDto.getUserId(),
                createAccountDto.getCurrencyId());
        return ResponseEntity.ok(account);
    }
}
