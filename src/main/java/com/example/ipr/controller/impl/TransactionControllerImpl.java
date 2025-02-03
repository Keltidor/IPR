package com.example.ipr.controller.impl;

import com.example.ipr.controller.TransactionController;
import com.example.ipr.domain.DepositRequestDto;
import com.example.ipr.domain.ExpenseTransactionDto;
import com.example.ipr.domain.TransferRequestDto;
import com.example.ipr.persist.entities.AccountEntity;
import com.example.ipr.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    @Override
    public ResponseEntity<List<ExpenseTransactionDto>> getExpenseTransactions(@PathVariable UUID accountId) {
        List<ExpenseTransactionDto> expenses = transactionService.getExpenseTransactions(accountId);
        return ResponseEntity.ok(expenses);
    }

    @Override
    public ResponseEntity<List<ExpenseTransactionDto>> getExceededLimitTransactions(@PathVariable UUID accountId) {
        List<ExpenseTransactionDto> transactions = transactionService.getExceededLimitTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @Override
    public ResponseEntity<AccountEntity> deposit(@RequestBody @Valid DepositRequestDto requestDto) {
        AccountEntity updatedAccount = transactionService.deposit(
                requestDto.getUserId(),
                requestDto.getAccountId(),
                requestDto.getAmount());
        return ResponseEntity.ok(updatedAccount);
    }

    @Override
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequestDto requestDto) {
        transactionService.transfer(
                requestDto.getSenderUserId(),
                requestDto.getSenderAccountId(),
                requestDto.getReceiverAccountId(),
                requestDto.getAmount()
        );
        return ResponseEntity.ok().build();
    }
}
