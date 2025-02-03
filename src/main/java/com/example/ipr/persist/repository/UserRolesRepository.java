package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRolesEntity, UUID> {
    List<UserRolesEntity> findByUserId(UUID userId);
}
