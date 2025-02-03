package com.example.ipr.service;

import com.example.ipr.domain.CheckTransferResponse;
import com.example.ipr.exceptions.BadRequestException;
import com.example.ipr.mapper.LimitMapper;
import com.example.ipr.persist.entities.LimitEntity;
import com.example.ipr.persist.entities.LimitSettingEntity;
import com.example.ipr.persist.repository.AccountRepository;
import com.example.ipr.persist.repository.LimitRepository;
import com.example.ipr.persist.repository.LimitSettingRepository;
import com.example.ipr.persist.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Сервис для управления лимитами на расходные операции.
 * Позволяет устанавливать лимиты, проверять их перед переводами и фиксировать изменения лимитов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {

    private final LimitRepository limitRepository;
    private final LimitSettingRepository limitSettingRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LimitMapper limitMapper;

    /**
     * Устанавливает или обновляет лимит на расходные операции для указанного счета.
     * Если лимит уже установлен, он будет обновлен на новую сумму.
     *
     * @param accountId      UUID счета, для которого устанавливается лимит
     * @param newLimitAmount Новая сумма лимита
     * @param currencyId     ID валюты, в которой установлен лимит
     * @return Обновленный или новый лимит
     * @throws BadRequestException Если счет не найден или новый лимит меньше уже потраченной суммы
     */
    @Transactional
    public LimitEntity setLimit(UUID accountId, BigDecimal newLimitAmount, Integer currencyId) {
        log.info("Запрос на установку лимита: accountId={}, newLimitAmount={}, currencyId={}",
                accountId, newLimitAmount, currencyId);

        if (!accountRepository.existsById(accountId)) {
            log.error("Ошибка: Счет с id {} не найден", accountId);
            throw new BadRequestException("Счет не найден");
        }

        BigDecimal totalSpent = getTotalSpentThisMonth(accountId);

        if (newLimitAmount.compareTo(totalSpent) < 0) {
            log.error("Ошибка: Новый лимит {} меньше уже потраченной суммы {}!", newLimitAmount, totalSpent);
            throw new BadRequestException("Нельзя установить лимит меньше уже потраченной суммы!");
        }

        LimitEntity limit = limitRepository.findByAccountId(accountId).orElse(null);

        if (Objects.isNull(limit)) {
            log.info("Создание нового лимита для accountId={}", accountId);
            limit = limitMapper.toLimitEntity(accountId, newLimitAmount, currencyId);
            limitRepository.save(limit);
        } else {
            log.warn("Обновление лимита для accountId={}", accountId);
            limit.setLimitAmount(newLimitAmount);
            limitRepository.save(limit);
        }

        LimitSettingEntity limitSetting = limitMapper.toLimitSettingEntity(limit, newLimitAmount);
        limitSettingRepository.save(limitSetting);
        log.info("История изменений лимита сохранена: {}", limitSetting);

        return limit;
    }

    /**
     * Возвращает общую сумму расходов за текущий месяц для указанного счета.
     *
     * @param accountId UUID счета, для которого вычисляется сумма расходов
     * @return Сумма расходов за текущий месяц, если данные отсутствуют — 0
     */
    private BigDecimal getTotalSpentThisMonth(UUID accountId) {
        return transactionRepository.getTotalSpentThisMonth(accountId);
    }

    /**
     * Проверяет, превышает ли сумма перевода установленный лимит.
     * Если лимит превышен, рассчитывается комиссия 5%.
     *
     * @param accountId UUID счета, с которого производится перевод
     * @param amount    Сумма перевода
     * @return Объект {@link CheckTransferResponse}, содержащий информацию о необходимости комиссии
     */
    @Transactional(readOnly = true)
    public CheckTransferResponse checkTransferLimit(UUID accountId, BigDecimal amount) {
        log.info("Проверка лимита перед переводом: accountId={}, amount={}", accountId, amount);

        // Получаем лимит для данного счёта
        LimitEntity limit = limitRepository.findByAccountId(accountId).orElse(null);
        if (Objects.isNull(limit)) {
            log.info("Лимит не установлен. Перевод возможен без ограничений.");
            return new CheckTransferResponse(true, false, BigDecimal.ZERO);
        }

        // Вычисляем начало текущего месяца
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        // Получаем общую сумму расходных операций за этот месяц
        BigDecimal totalSpentThisMonth = transactionRepository.getTotalSpentForMonth(accountId, startOfMonth);

        BigDecimal remainingLimit = limit.getLimitAmount().subtract(totalSpentThisMonth);

        log.info("Сумма расходов за месяц: {}, установленный лимит: {}", totalSpentThisMonth, limit.getLimitAmount());

        // Проверяем, превышает ли сумма новый лимит
        if (amount.compareTo(remainingLimit) > 0) {
            // Если превышает лимит, добавляем комиссию 5%
            BigDecimal feeAmount = amount.multiply(BigDecimal.valueOf(0.05));
            log.warn("Лимит превышен! К переводу добавлена комиссия {}", feeAmount);

            return new CheckTransferResponse(true, true, feeAmount);
        }

        return new CheckTransferResponse(true, false, BigDecimal.ZERO);
    }
}
