package com.example.ipr.mapper;

import com.example.ipr.persist.entities.LimitEntity;
import com.example.ipr.persist.entities.LimitSettingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface LimitMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "accountId", target = "accountId"),
            @Mapping(source = "limitAmount", target = "limitAmount"),
            @Mapping(source = "currencyId", target = "currencyId"),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    })
    LimitEntity toLimitEntity(UUID accountId, BigDecimal limitAmount, Integer currencyId);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "limit", target = "limit"),
            @Mapping(source = "limitAmount", target = "limitAmount"),
            @Mapping(target = "limitSetDate", expression = "java(LocalDateTime.now())")
    })
    LimitSettingEntity toLimitSettingEntity(LimitEntity limit, BigDecimal limitAmount);
}
