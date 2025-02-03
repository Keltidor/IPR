package com.example.ipr.mapper;

import com.example.ipr.domain.RegistrationDto;
import com.example.ipr.persist.entities.CountryOfResidenceEntity;
import com.example.ipr.persist.entities.PassportDataEntity;
import com.example.ipr.persist.entities.UserEntity;
import com.example.ipr.persist.entities.UserRolesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mappings({
            @Mapping(source = "dto.firstName", target = "firstName"),
            @Mapping(source = "dto.lastName", target = "lastName"),
            @Mapping(source = "dto.middleName", target = "middleName"),
            @Mapping(source = "dto.mobilePhone", target = "mobilePhone"),
            @Mapping(source = "dto.email", target = "email"),
            @Mapping(source = "dto.encodedPassword", target = "encodedPassword"),
            @Mapping(source = "passportData.id", target = "passportDataId"),
            @Mapping(source = "countryOfResidence.id", target = "countryOfResidenceId"),
            @Mapping(target = "clientStatus", constant = "true"),
            @Mapping(target = "id", ignore = true) // Hibernate сам генерирует id
    })
    UserEntity toUserEntity(RegistrationDto dto, PassportDataEntity passportData, CountryOfResidenceEntity countryOfResidence);

    @Mappings({
            @Mapping(source = "dto.identificationPassportNumber", target = "identificationPassportNumber"),
            @Mapping(source = "dto.issuanceDate", target = "issuanceDate"),
            @Mapping(source = "dto.expiryDate", target = "expiryDate"),
            @Mapping(source = "dto.birthDate", target = "birthDate"),
            @Mapping(source = "dto.nationality", target = "nationality"),
            @Mapping(target = "id", ignore = true) // Hibernate сам генерирует id
    })
    PassportDataEntity toPassportDataEntity(RegistrationDto dto);

    @Mappings({
            @Mapping(source = "userId", target = "userId"), // Явно передаем userId
            @Mapping(target = "id", ignore = true), // Hibernate сам генерирует id
            @Mapping(target = "role", ignore = true) // Роль устанавливается в сервисе
    })
    UserRolesEntity toUserRoleEntity(UUID userId);
}
