package com.example.ipr.mapper;

import com.example.ipr.domain.ExpenseTransactionDto;
import com.example.ipr.persist.entities.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface TransactionMapper {

    @Mappings({
            @Mapping(source = "accountId", target = "accountId"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "currencyId", target = "currencyId"),
            @Mapping(target = "transactionDate", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "description", source = "description")
    })
    TransactionEntity toTransactionEntity(UUID accountId, BigDecimal amount, Integer currencyId, String description);

    @Mappings({
            @Mapping(source = "id", target = "transactionId"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "currencyId", target = "currencyId"),
            @Mapping(source = "transactionDate", target = "transactionDate"),
            @Mapping(source = "description", target = "description")
    })
    ExpenseTransactionDto toExpenseTransactionDto(TransactionEntity transactionEntity);

    List<ExpenseTransactionDto> toExpenseTransactionDtoList(List<TransactionEntity> transactions);
}
