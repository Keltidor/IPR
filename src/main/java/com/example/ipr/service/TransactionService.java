package com.example.ipr.service;

import com.example.ipr.domain.CheckTransferResponse;
import com.example.ipr.domain.ExpenseTransactionDto;
import com.example.ipr.exceptions.BadRequestException;
import com.example.ipr.mapper.TransactionMapper;
import com.example.ipr.persist.entities.AccountEntity;
import com.example.ipr.persist.entities.TransactionEntity;
import com.example.ipr.persist.repository.AccountRepository;
import com.example.ipr.persist.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сервис для управления транзакциями.
 * Позволяет выполнять переводы между счетами, пополнять баланс и получать информацию о расходах.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final LimitService limitService;

    /**
     * Получает список всех расходных транзакций для указанного счета.
     *
     * @param accountId UUID счета, для которого запрашиваются транзакции
     * @return Список объектов {@link ExpenseTransactionDto}, представляющих расходные транзакции
     */
    public List<ExpenseTransactionDto> getExpenseTransactions(UUID accountId) {
        List<TransactionEntity> expenses = transactionRepository.findExpensesByAccountId(accountId);
        return transactionMapper.toExpenseTransactionDtoList(expenses);
    }

    /**
     * Получает список транзакций, превысивших установленный лимит.
     *
     * @param accountId UUID счета, для которого выполняется поиск
     * @return Список объектов {@link ExpenseTransactionDto}, содержащих информацию о транзакциях с превышением лимита
     */
    public List<ExpenseTransactionDto> getExceededLimitTransactions(UUID accountId) {
        log.info("Запрос на получение транзакций, превысивших лимит, для accountId={}", accountId);

        List<TransactionEntity> exceededTransactions = transactionRepository.findTransactionsExceededLimit(accountId);

        if (exceededTransactions.isEmpty()) {
            log.info("Нет транзакций, превысивших лимит, для accountId={}", accountId);
            return Collections.emptyList();
        }

        log.info("Найдено {} транзакций, превысивших лимит", exceededTransactions.size());
        return transactionMapper.toExpenseTransactionDtoList(exceededTransactions);
    }

    /**
     * Пополняет баланс указанного счета.
     *
     * @param userId    UUID пользователя, выполняющего пополнение
     * @param accountId UUID счета, который пополняется
     * @param amount    Сумма пополнения
     * @return Обновленный объект {@link AccountEntity} с новым балансом
     * @throws BadRequestException Если сумма пополнения некорректна или пользователь не владеет счетом
     */
    @Transactional
    public AccountEntity deposit(UUID userId, UUID accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Пополнение счета невозможно: сумма должна быть больше 0");
            throw new BadRequestException("Сумма пополнения должна быть больше 0");
        }

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Счет не найден"));

        if (!account.getUserId().equals(userId)) {
            log.error("Пополнение счета невозможно: Пользователь {} не владеет счетом {}", userId, accountId);
            throw new BadRequestException("Вы не владеете этим счетом");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        log.info("Счет {} пополнен на сумму {}. Новый баланс: {}", accountId, amount, account.getBalance());
        return account;
    }

    /**
     * Выполняет перевод средств между счетами с учетом лимитов и возможных комиссий.
     *
     * @param senderUserId      UUID пользователя, отправляющего средства
     * @param senderAccountId   UUID счета отправителя
     * @param receiverAccountId UUID счета получателя
     * @param amount            Сумма перевода
     * @throws BadRequestException Если перевод невозможен (недостаточно средств, неверные реквизиты и т. д.)
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transfer(UUID senderUserId, UUID senderAccountId, UUID receiverAccountId, BigDecimal amount) {
        validateTransfer(senderUserId, senderAccountId, receiverAccountId, amount);

        AccountEntity senderAccount = accountRepository.findByIdForUpdate(senderAccountId)
                .orElseThrow(() -> new BadRequestException("Счет отправителя не найден"));
        AccountEntity receiverAccount = accountRepository.findByIdForUpdate(receiverAccountId)
                .orElseThrow(() -> new BadRequestException("Счет получателя не найден"));

        // Проверяем лимит перед переводом
        CheckTransferResponse checkResponse = limitService.checkTransferLimit(senderAccountId, amount);
        BigDecimal finalAmount = amount;
        BigDecimal feeAmount = BigDecimal.ZERO;
        boolean feeApplied = false;

        if (checkResponse.isRequiresFee()) {
            feeAmount = checkResponse.getFeeAmount();
            finalAmount = finalAmount.add(feeAmount);
            feeApplied = true;
            log.warn("Лимит превышен! К переводу добавлена комиссия {}", feeAmount);
        }

        // Проверяем баланс с учетом комиссии
        if (senderAccount.getBalance().compareTo(finalAmount) < 0) {
            throw new BadRequestException("Недостаточно средств на счету!");
        }

        // Обновляем балансы
        updateBalances(senderAccount, receiverAccount, amount, feeAmount);

        // Сохраняем транзакции (перевод + комиссия)
        saveTransactions(senderAccountId, receiverAccountId, amount, feeAmount, senderAccount.getCurrencyId(), feeApplied);

        log.info("Перевод {} со счета {} на счет {} выполнен. Новый баланс отправителя: {}, получателя: {}",
                amount, senderAccountId, receiverAccountId, senderAccount.getBalance(), receiverAccount.getBalance());
    }

    /**
     * Валидирует корректность перевода средств.
     *
     * @param senderUserId      UUID пользователя, выполняющего перевод
     * @param senderAccountId   UUID счета отправителя
     * @param receiverAccountId UUID счета получателя
     * @param amount            Сумма перевода
     * @throws BadRequestException Если перевод невозможен (ошибки в реквизитах, недостаточно средств и т. д.)
     */
    private void validateTransfer(UUID senderUserId, UUID senderAccountId, UUID receiverAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Сумма перевода должна быть больше 0");
        }

        if (!accountRepository.existsById(senderAccountId)) {
            throw new BadRequestException("Счет отправителя не найден");
        }

        if (!accountRepository.existsById(receiverAccountId)) {
            throw new BadRequestException("Счет получателя не найден");
        }

        AccountEntity senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new BadRequestException("Счет отправителя не найден"));
        AccountEntity receiverAccount = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new BadRequestException("Счет получателя не найден"));

        if (!senderAccount.getUserId().equals(senderUserId)) {
            throw new BadRequestException("Вы не владеете этим счетом");
        }

        if (!senderAccount.getCurrencyId().equals(receiverAccount.getCurrencyId())) {
            throw new BadRequestException("Перевод возможен только между счетами с одинаковой валютой");
        }

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Недостаточно средств на счету");
        }
    }

    /**
     * Обновляет балансы счетов отправителя и получателя.
     *
     * @param senderAccount  Счет отправителя
     * @param receiverAccount Счет получателя
     * @param amount         Сумма перевода
     * @param feeAmount      Комиссия за превышение лимита (если применима)
     */
    private void updateBalances(AccountEntity senderAccount, AccountEntity receiverAccount, BigDecimal amount, BigDecimal feeAmount) {
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount.add(feeAmount))); // Учитываем комиссию
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount)); // Получатель получает только перевод

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    /**
     * Сохраняет информацию о транзакции в базе данных.
     *
     * @param senderAccountId   UUID счета отправителя
     * @param receiverAccountId UUID счета получателя
     * @param amount            Сумма перевода
     * @param feeAmount         Комиссия (если применима)
     * @param currencyId        ID валюты транзакции
     * @param feeApplied        Применена ли комиссия
     */
    private void saveTransactions(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, BigDecimal feeAmount,
                                  Integer currencyId, boolean feeApplied) {
        if (Objects.isNull(currencyId)) {
            throw new IllegalStateException("Currency ID не может быть null!");
        }

        TransactionEntity senderTransaction = transactionMapper.toTransactionEntity(
                senderAccountId, amount.negate(), currencyId, "Перевод на счет " + receiverAccountId);

        TransactionEntity receiverTransaction = transactionMapper.toTransactionEntity(
                receiverAccountId, amount, currencyId, "Перевод от счета " + senderAccountId);

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
    }
}
