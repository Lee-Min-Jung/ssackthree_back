package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.StoreProfileFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreProfileFileRepository extends JpaRepository<StoreProfileFileEntity, Long> {
    Optional<StoreProfileFileEntity> findByStoreEntityId(Long id);

}
