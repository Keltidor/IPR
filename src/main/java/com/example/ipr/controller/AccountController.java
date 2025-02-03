package com.example.ipr.controller;

import com.example.ipr.domain.CreateAccountDto;
import com.example.ipr.exceptions.ErrorDto;
import com.example.ipr.persist.entities.AccountEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер для управления банковскими счетами.
 * Позволяет выполнять операции, связанные с созданием счетов.
 */
public interface AccountController {

    /**
     * Создает новый банковский счет для пользователя.
     *
     * @param createAccountDto Объект DTO, содержащий информацию для создания счета (ID пользователя, ID валюты).
     * @return Объект {@link ResponseEntity}, содержащий созданный {@link AccountEntity}.
     */
    @ApiOperation(value = "Создание счета", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<AccountEntity> createAccount(@Valid @RequestBody @NotNull CreateAccountDto createAccountDto);
}
