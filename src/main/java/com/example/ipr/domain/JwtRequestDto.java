package com.example.ipr.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс, который пользователь будет присылать,
 * чтобы получить JWT токен. Он содержит поля: email пользователя и его пароль.
 */

@Setter
@Getter
public class JwtRequestDto {

    private String email;
    private String encodedPassword;
}
