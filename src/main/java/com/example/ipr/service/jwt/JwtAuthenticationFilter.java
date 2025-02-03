package com.example.ipr.service.jwt;

import com.example.ipr.persist.entities.UserEntity;
import com.example.ipr.persist.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Фильтр аутентификации JWT, который проверяет наличие и валидность токена в каждом запросе.
 * Если токен действителен, аутентифицирует пользователя в контексте безопасности Spring.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProviderService jwtProviderService;
    private final UserRepository userRepository;

    /**
     * Выполняет фильтрацию входящего HTTP-запроса, проверяя наличие и корректность JWT-токена.
     * Если токен действителен, аутентифицирует пользователя.
     *
     * @param request     HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain Цепочка фильтров безопасности
     * @throws ServletException В случае ошибки сервлета
     * @throws IOException      В случае ошибки ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtProviderService.validateAccessToken(token)) {
            String userEmail = jwtProviderService.getUserEmailFromToken(token).orElse(null);

            if (userEmail != null) {
                UserEntity user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT-токен из заголовка Authorization HTTP-запроса.
     * Ожидает токен в формате "Bearer <token>".
     *
     * @param request HTTP-запрос
     * @return JWT-токен, если присутствует в заголовке; иначе null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

