package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileFileRepository extends JpaRepository<CustomerProfileFileEntity, Long> {
    Optional<CustomerProfileFileEntity> findByUserEntityId(Long id);
}
