package com.example.ipr.persist.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "limit_settings", schema = "ipr")
public class LimitSettingEntity extends BaseUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "limit_id", nullable = false)
    private LimitEntity limit;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;

    @Column(name = "limit_set_date", nullable = false)
    private LocalDateTime limitSetDate;
}
