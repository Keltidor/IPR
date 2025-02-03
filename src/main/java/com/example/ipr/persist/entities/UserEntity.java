package com.example.ipr.persist.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "ipr")
public class UserEntity extends BaseUuidEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "mobile_phone", nullable = false, unique = true, length = 15)
    private String mobilePhone;

    @Column(name = "passport_data_id", nullable = false)
    private UUID passportDataId;

    @Column(name = "client_status", nullable = false)
    private Boolean clientStatus;

    @Column(name = "country_of_residence_id", nullable = false)
    private Integer countryOfResidenceId;

    @Column(name = "encoded_password", nullable = false)
    private String encodedPassword;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
