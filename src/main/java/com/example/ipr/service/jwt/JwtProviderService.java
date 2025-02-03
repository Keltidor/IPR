package com.example.ipr.service.jwt;

import com.example.ipr.persist.entities.UserEntity;
import com.example.ipr.persist.repository.UserRolesRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Компонент JwtProvider. Он будет генерировать и валидировать access и refresh токены.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderService {

    private final UserRolesRepository userRolesRepository;

    private SecretKey jwtAccessSecret;
    private SecretKey jwtRefreshSecret;

    @Value("${jwt.secret.access}")
    private String jwtAccessSecretKey;

    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecretKey;

    @Value("${jwt.expiration.access}")
    private long accessTokenValidityMinutes;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenValidityMinutes;

    /**
     * При запуске инициализируем секретные ключи. Для подписи и валидации токена.
     * Это позволит создавать отдельные сервисы с бизнес-логикой которые не будут выдавать токены,
     * но зная ключ от access токена смогут их валидировать, при этом не зная ключ от refresh токена в целях безопасности.
     * <p>
     * поле jwtAccessSecret Используется для генерации access токена.
     * поле jwtRefreshSecret Используется для генерации refresh токена.
     */
    @PostConstruct
    public void init() {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecretKey));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecretKey));
    }

    /**
     * Принимает объект пользователя и генерирует access токен для него с определенным временем жизни
     *
     * @param user Объект пользователя
     * @return access токен, в который мы указываем email пользователя, дату, до которой токен валиден,
     * алгоритм шифрования и роль пользователя.
     */
    public String generateAccessToken(@NonNull UserEntity user) {
        final Instant expirationInstant = LocalDateTime.now()
                .plusMinutes(accessTokenValidityMinutes)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        final Date expiration = Date.from(expirationInstant);

        List<String> roleStrings = userRolesRepository.findByUserId(user.getId())
                .stream()
                .map(userRolesEntity -> userRolesEntity.getRole().name())
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(expiration)
                .claim("userId", user.getId().toString())
                .claim("roles", roleStrings)
                .signWith(jwtAccessSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Аналогичен методу {@link #generateAccessToken}, только не передаем туда claims и указываем большее время жизни.
     */
    public String generateRefreshToken(@NonNull UserEntity user) {
        final Instant refreshExpirationInstant = LocalDateTime.now()
                .plusMinutes(refreshTokenValidityMinutes)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        final Date expiration = Date.from(refreshExpirationInstant);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(expiration)
                .claim("userId", user.getId().toString())
                .signWith(jwtRefreshSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Отвечает за проверку валидности токена. Если истек срок действия токена, или неправильно подписан,
     * то в лог запишется соответсвующее сообщение, а метод вернет false.
     */
    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    /**
     * Аналогичен методу {@link #validateAccessToken}.
     */
    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    /**
     * Универсальный метод получения Claims из токена.
     */
    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Универсальный метод валидации токена.
     */
    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            getClaims(token, secret);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("Invalid token", e);
        }
        return false;
    }

    /**
     * Получает почту юзера из токена.
     * @param token токен
     * @return claims из токена
     */
    public Optional<String> getUserEmailFromToken(String token) {
        try {
            return Optional.of(getClaims(token, jwtAccessSecret).getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
