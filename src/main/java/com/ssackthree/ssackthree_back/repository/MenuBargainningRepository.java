package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuBargainningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuBargainningRepository extends JpaRepository<MenuBargainningEntity, Long> {
}
