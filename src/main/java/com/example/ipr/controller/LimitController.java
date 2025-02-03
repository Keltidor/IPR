package com.example.ipr.controller;

import com.example.ipr.domain.CheckTransferDto;
import com.example.ipr.domain.CheckTransferResponse;
import com.example.ipr.domain.SetLimitDto;
import com.example.ipr.exceptions.ErrorDto;
import com.example.ipr.persist.entities.LimitEntity;
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
 * Контроллер для управления лимитами пользователей.
 * Позволяет устанавливать лимиты на счета и проверять возможность перевода с учетом установленного лимита.
 */
public interface LimitController {

    /**
     * Устанавливает лимит на указанный счет.
     *
     * @param setLimitDto DTO-объект, содержащий данные для установки лимита (ID счета, сумма лимита, ID валюты).
     * @return Объект {@link ResponseEntity}, содержащий установленный {@link LimitEntity}.
     */
    @ApiOperation(value = "Установка лимита", produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/set", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<LimitEntity> setLimit(@Valid @RequestBody @NotNull SetLimitDto setLimitDto);

    /**
     * Проверяет, превышает ли сумма перевода установленный лимит и требуется ли комиссия.
     *
     * @param request DTO-объект, содержащий информацию о переводе (ID счета, сумма перевода).
     * @return Объект {@link ResponseEntity}, содержащий результат проверки лимита {@link CheckTransferResponse}.
     */
    @ApiOperation(value = "Проверка лимита перед переводом", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/check-transfer", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CheckTransferResponse> checkTransfer(@Valid @RequestBody @NotNull CheckTransferDto request);
}
