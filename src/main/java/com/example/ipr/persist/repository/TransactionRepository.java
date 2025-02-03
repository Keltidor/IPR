package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.TransactionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.amount < 0 ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findExpensesByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT t FROM TransactionEntity t " +
            "WHERE t.accountId = :accountId " +
            "AND t.description LIKE '%Комиссия за превышение лимита%'")
    List<TransactionEntity> findTransactionsExceededLimit(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.accountId = :accountId " +
            "AND t.transactionDate >= date_trunc('month', CURRENT_DATE)")
    BigDecimal getTotalSpentThisMonth(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.accountId = :accountId " +
            "AND t.amount < 0 " +
            "AND t.transactionDate >= :startOfMonth")
   BigDecimal getTotalSpentForMonth(@Param("accountId") UUID accountId, @Param("startOfMonth") LocalDateTime startOfMonth);
}
