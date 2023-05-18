package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuStatusRepository extends JpaRepository<MenuStatusEntity, Long> {

    Optional<MenuStatusEntity> findByMenuEntityId(long id);
}
