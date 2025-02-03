package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.CountryOfResidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryOfResidenceRepository extends JpaRepository<CountryOfResidenceEntity, Integer> {

}
