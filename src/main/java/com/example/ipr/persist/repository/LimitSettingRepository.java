package com.example.ipr.persist.repository;

import com.example.ipr.persist.entities.LimitSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LimitSettingRepository extends JpaRepository<LimitSettingEntity, UUID> {

}
