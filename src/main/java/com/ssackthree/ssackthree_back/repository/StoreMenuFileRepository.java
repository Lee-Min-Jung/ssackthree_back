package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.StoreMenuFileEntity;
import com.ssackthree.ssackthree_back.entity.StoreProfileFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreMenuFileRepository extends JpaRepository<StoreMenuFileEntity, Long> {
    Optional<StoreMenuFileEntity[]> findByStoreEntityId(Long id);
    void deleteByStoreEntityId(Long id);

}
