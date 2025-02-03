package com.example.ipr.service;

import com.example.ipr.exceptions.BadRequestException;
import com.example.ipr.mapper.AccountMapper;
import com.example.ipr.persist.entities.AccountEntity;
import com.example.ipr.persist.repository.AccountRepository;
import com.example.ipr.persist.repository.CurrencyRepository;
import com.example.ipr.persist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для управления банковскими счетами пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final CurrencyRepository currencyRepository;

    /**
     * Создает новый банковский счет для указанного пользователя в заданной валюте.
     * Проверяет наличие пользователя и валюты перед созданием счета.
     *
     * @param userId     UUID пользователя, которому создается счет
     * @param currencyId ID валюты, в которой будет открыт счет
     * @return Созданный банковский счет
     * @throws BadRequestException Если пользователь или валюта не найдены в системе
     */
    @Transactional
    public AccountEntity createAccount(UUID userId, Integer currencyId) {
        if (!userRepository.existsById(userId)) {
            log.error("Пользователь с id {} не найден", userId);
            throw new BadRequestException("Пользователь не найден");
        }

        if (!currencyRepository.existsById(currencyId)) {
            log.error("Валюта с id {} не найдена", currencyId);
            throw new BadRequestException("Валюта не найдена");
        }

        AccountEntity account = accountMapper.toAccountEntity(userId, currencyId);

        AccountEntity savedAccount = accountRepository.save(account);
        log.info("Создан новый счет для пользователя: {}", savedAccount.getUserId());

        return savedAccount;
    }
}
