package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Integer> {

}
