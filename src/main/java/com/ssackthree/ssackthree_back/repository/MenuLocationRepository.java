package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuLocationRepository extends JpaRepository<MenuLocationEntity, Long> {
}
