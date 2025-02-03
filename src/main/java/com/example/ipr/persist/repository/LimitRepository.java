package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.LimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LimitRepository extends JpaRepository<LimitEntity, UUID> {

    Optional<LimitEntity> findByAccountId(UUID accountId);
}
