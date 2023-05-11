package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.UserLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocationEntity, Long> {
}
