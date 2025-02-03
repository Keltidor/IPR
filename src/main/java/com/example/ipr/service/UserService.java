package com.example.ipr.service;

import com.example.ipr.domain.JwtRequestDto;
import com.example.ipr.domain.JwtResponseDto;
import com.example.ipr.domain.RegistrationDto;
import com.example.ipr.enums.Role;
import com.example.ipr.exceptions.BadRequestException;
import com.example.ipr.mapper.RegistrationMapper;
import com.example.ipr.persist.entities.CountryOfResidenceEntity;
import com.example.ipr.persist.entities.PassportDataEntity;
import com.example.ipr.persist.entities.UserEntity;
import com.example.ipr.persist.entities.UserRolesEntity;
import com.example.ipr.persist.repository.CountryOfResidenceRepository;
import com.example.ipr.persist.repository.PassportDataRepository;
import com.example.ipr.persist.repository.UserRepository;
import com.example.ipr.persist.repository.UserRolesRepository;
import com.example.ipr.service.jwt.JwtProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис, отвечающий за регистрацию и управление пользователями.
 * Этот класс предоставляет методы для регистрации пользователей, входа в систему,
 * деактивации учетных записей и восстановления аккаунтов пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PassportDataRepository passportDataRepository;
    private final UserRolesRepository userRolesRepository;
    private final CountryOfResidenceRepository countryOfResidenceRepository;
    private final RegistrationMapper registrationMapper;
    private final JwtProviderService jwtProviderService;

    /**
     * Регистрирует нового пользователя в системе на основе предоставленных данных.
     * Валидирует данные регистрации, генерирует уникальные идентификаторы и сохраняет
     * данные пользователя в базе данных.
     *
     * @param registrationDto Данные регистрации пользователя.
     * @return Зарегистрированный профиль пользователя.
     * @throws BadRequestException Если данные регистрации неверные.
     */
    @Transactional
    public UserEntity registerUser(RegistrationDto registrationDto) {
        if (!validateRegistrationData(registrationDto)) {
            log.error("Неверные данные регистрации: {}", registrationDto);
            throw new BadRequestException("Неверные данные регистрации");
        }

        PassportDataEntity passportData = registrationMapper.toPassportDataEntity(registrationDto);
        passportDataRepository.save(passportData);

        CountryOfResidenceEntity country = countryOfResidenceRepository.findById(registrationDto.getCountryOfResidenceId())
                .orElseThrow(() -> new IllegalArgumentException("Ошибка при выборе страны"));

        UserEntity user = registrationMapper.toUserEntity(registrationDto, passportData, country);
        user.setClientStatus(true);

        userRepository.save(user);

        UserRolesEntity userRoles = registrationMapper.toUserRoleEntity(user.getId());
        userRoles.setRole(Role.USER);
        userRolesRepository.save(userRoles);

        log.info("Пользователь успешно зарегистрирован: {}", user.getEmail());

        return user;
    }

    /**
     * Проверяет идентификационные данные пользователя и генерирует токены доступа и обновления
     * в случае успешного входа. Если вход не удался, выбрасывает исключение.
     *
     * @param jwtRequestDto Запрос входа пользователя.
     * @return Ответ с токенами доступа и обновления.
     * @throws BadRequestException Если вход не удался из-за неверных данных.
     */
    public JwtResponseDto login(JwtRequestDto jwtRequestDto) {
        UserEntity user = userRepository.findByEmail(jwtRequestDto.getEmail())
                .orElseThrow(() -> new BadRequestException("Неверные учетные данные"));

        if (!user.getEncodedPassword().equals(jwtRequestDto.getEncodedPassword())) {
            log.error("Ошибка входа: Неверный пароль для {}", jwtRequestDto.getEmail());
            throw new BadRequestException("Неверные учетные данные");
        }

        // Генерируем токены
        String accessToken = jwtProviderService.generateAccessToken(user);
        String refreshToken = jwtProviderService.generateRefreshToken(user);

        log.info("Пользователь {} успешно вошел в систему", user.getEmail());

        return new JwtResponseDto(accessToken, refreshToken);
    }

    /**
     * Проверяет данные регистрации на валидность. Возвращает true, если данные верные.
     *
     * @param registrationDto Данные регистрации пользователя.
     * @return true, если данные верные; в противном случае - false.
     */
    public boolean validateRegistrationData(RegistrationDto registrationDto) {
        // Проверка серии и номера паспорта (должны содержать только цифры, формат ввода: "XXXX XXX XXX", или XXXXXXXXXX)
        if (!registrationDto.getIdentificationPassportNumber().matches("\\d{4} \\d{3} \\d{3}|\\d{10}")) {
            log.error("Неверные идентификационные данные: " + registrationDto.getIdentificationPassportNumber());
            return false;
        }

        // Проверка ФИО (должны содержать только буквы)
        if (!registrationDto.getFirstName().matches("[A-Za-zА-Яа-я]+") ||
                !registrationDto.getLastName().matches("[A-Za-zА-Яа-я]+") ||
                !registrationDto.getMiddleName().matches("[A-Za-zА-Яа-я]+")) {
            log.error("Неверные данные ФИО: " + registrationDto.getFirstName() +
                    " " + registrationDto.getLastName() + " " + registrationDto.getMiddleName());
            return false;
        }

        return true; // Все данные прошли проверку
    }
}
