package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
}
