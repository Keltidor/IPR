package com.example.ipr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationDto {

    @JsonProperty(value = "first_name", required = true)
    private final String firstName;

    @JsonProperty(value = "last_name", required = true)
    private final String lastName;

    @JsonProperty(value = "middle_name")
    private final String middleName;

    @JsonProperty(value = "country_of_residence_id", required = true)
    private final Integer countryOfResidenceId;

    @JsonProperty(value = "mobile_phone", required = true)
    private final String mobilePhone;

    @JsonProperty(value = "identification_passport_number", required = true)
    private final String identificationPassportNumber;

    @JsonProperty(value = "issuance_date", required = true)
    private LocalDate issuanceDate;

    @JsonProperty(value = "expiry_date", required = true)
    private LocalDate expiryDate;

    @JsonProperty(value = "birth_date", required = true)
    private LocalDate birthDate;

    @JsonProperty(value = "nationality", required = true)
    private final String nationality;

    @JsonProperty(value = "encoded_password", required = true)
    private final String encodedPassword;

    @JsonProperty(value = "email", required = true)
    private final String email;
}
