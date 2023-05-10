package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.StoreEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findByUserEntityId(long id);
}
