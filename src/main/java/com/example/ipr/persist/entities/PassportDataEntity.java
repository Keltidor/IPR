package com.example.ipr.persist.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "passport_data", schema = "ipr")
public class PassportDataEntity extends BaseUuidEntity {

    @Column(name = "identification_passport_number", nullable = false, length = 12)
    private String identificationPassportNumber;

    @Column(name = "issuance_date", nullable = false)
    private LocalDate issuanceDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "nationality", nullable = false, length = 50)
    private String nationality;
}
