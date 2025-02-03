package com.example.ipr.mapper;

import com.example.ipr.persist.entities.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {BigDecimal.class, LocalDateTime.class})
public interface AccountMapper {

    @Mappings({
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "currencyId", target = "currencyId"),
            @Mapping(target = "balance", expression = "java(BigDecimal.ZERO)"), // Баланс всегда 0
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"), // Устанавливаем текущее время
            @Mapping(target = "id", ignore = true) // Hibernate сам создаст UUID
    })
    AccountEntity toAccountEntity(UUID userId, Integer currencyId);
}
