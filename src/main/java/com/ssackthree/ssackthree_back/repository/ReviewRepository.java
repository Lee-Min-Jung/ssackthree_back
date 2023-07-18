package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<List<ReviewEntity>> findByStoreEntityId(long id);
}
