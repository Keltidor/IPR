package com.example.ipr.controller;

import com.example.ipr.domain.DepositRequestDto;
import com.example.ipr.domain.ExpenseTransactionDto;
import com.example.ipr.domain.TransferRequestDto;
import com.example.ipr.exceptions.ErrorDto;
import com.example.ipr.persist.entities.AccountEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления транзакциями.
 * Позволяет получать список расходных операций, транзакции, превысившие лимит, а также выполнять переводы и пополнение счета.
 */
public interface TransactionController {

    /**
     * Получает список всех расходных операций для указанного счета.
     *
     * @param accountId UUID счета, для которого запрашиваются расходные операции.
     * @return Список {@link ExpenseTransactionDto}, содержащий данные о расходных транзакциях.
     */
    @ApiOperation(value = "Получение списка расходных операций", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @GetMapping(value = "/expenses/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ExpenseTransactionDto>> getExpenseTransactions(@PathVariable UUID accountId);

    /**
     * Получает список транзакций, сумма которых превысила установленный лимит.
     *
     * @param accountId UUID счета, для которого запрашиваются транзакции, превысившие лимит.
     * @return Список {@link ExpenseTransactionDto}, содержащий данные о транзакциях с превышенным лимитом.
     */
    @ApiOperation(value = "Получение транзакций, превысивших лимит", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @GetMapping(value = "/exceeded-limit/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ExpenseTransactionDto>> getExceededLimitTransactions(@PathVariable UUID accountId);

    /**
     * Пополняет баланс указанного счета.
     *
     * @param requestDto DTO-объект, содержащий данные о пополнении счета (ID пользователя, ID счета, сумма пополнения).
     * @return Объект {@link ResponseEntity}, содержащий обновленный {@link AccountEntity}.
     */
    @ApiOperation(value = "Пополнение счета", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/deposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountEntity> deposit(@RequestBody @Valid DepositRequestDto requestDto);

    /**
     * Выполняет перевод денежных средств между счетами.
     *
     * @param requestDto DTO-объект, содержащий данные о переводе (ID отправителя, ID счета отправителя, ID счета получателя, сумма перевода).
     * @return Объект {@link ResponseEntity} без содержимого в случае успешного выполнения операции.
     */
    @ApiOperation(value = "Перевод между счетами", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequestDto requestDto);
}
