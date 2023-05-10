package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.StoreLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreLocationRepository extends JpaRepository<StoreLocationEntity, Long> {
    Optional<StoreLocationEntity> findByStoreEntityId(long storeId);
}
