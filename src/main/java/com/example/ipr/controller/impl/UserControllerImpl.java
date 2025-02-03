package com.example.ipr.controller.impl;

import com.example.ipr.controller.UserController;
import com.example.ipr.domain.JwtRequestDto;
import com.example.ipr.domain.JwtResponseDto;
import com.example.ipr.domain.RegistrationDto;
import com.example.ipr.persist.entities.UserEntity;
import com.example.ipr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserEntity> registration(RegistrationDto registrationDto) {
        UserEntity registeredUser = userService.registerUser(registrationDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Override
    public ResponseEntity<JwtResponseDto> login(JwtRequestDto jwtRequestDto) {
        JwtResponseDto jwtResponseDto = userService.login(jwtRequestDto);
        return ResponseEntity.ok(jwtResponseDto);
    }
}
