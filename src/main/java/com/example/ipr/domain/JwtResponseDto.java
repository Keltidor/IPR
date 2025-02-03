package com.example.ipr.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Будет содержать access и refresh токены. Этот объект будет возвращаться в ответ пользователю.
 */
@Getter
@AllArgsConstructor
public class JwtResponseDto {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
