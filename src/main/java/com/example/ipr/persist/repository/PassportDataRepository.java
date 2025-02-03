package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.PassportDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PassportDataRepository extends JpaRepository<PassportDataEntity, UUID> {

}
