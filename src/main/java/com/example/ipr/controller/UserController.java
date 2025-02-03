package com.example.ipr.controller;

import com.example.ipr.domain.JwtRequestDto;
import com.example.ipr.domain.JwtResponseDto;
import com.example.ipr.domain.RegistrationDto;
import com.example.ipr.exceptions.ErrorDto;
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
 * Контроллер для управления пользователями.
 * Позволяет выполнять регистрацию новых пользователей и аутентификацию.
 */
public interface UserController {

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param registrationDto DTO-объект, содержащий данные для регистрации пользователя.
     * @return {@link ResponseEntity} с данными о зарегистрированном пользователе или сообщением об ошибке.
     */
    @ApiOperation(value = "Регистрация пользователя", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/registration", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<?> registration(@Valid @RequestBody @NotNull RegistrationDto registrationDto);

    /**
     * Аутентифицирует пользователя в системе.
     * Генерирует токены доступа и обновления при успешном входе.
     *
     * @param jwtRequestDto DTO-объект, содержащий учетные данные пользователя (email и пароль).
     * @return {@link ResponseEntity} с токенами доступа и обновления.
     */
    @ApiOperation(value = "Аутентификация пользователя", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorDto.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorDto.class)
    })
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<JwtResponseDto> login(@RequestBody JwtRequestDto jwtRequestDto);
}
