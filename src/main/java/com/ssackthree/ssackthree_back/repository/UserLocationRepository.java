package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.UserLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocationEntity, Long> {
    Optional<UserLocationEntity> findTopByUserEntityIdOrderByCreatedDateDesc(Long userId);
    Optional<List<UserLocationEntity>> findTop5ByUserEntityIdOrderByCreatedDateDesc(Long userId);
}
