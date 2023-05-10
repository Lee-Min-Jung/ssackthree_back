package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuFileRepository extends JpaRepository<MenuFileEntity, Long> {
    MenuFileEntity findFirstByMenuEntityId(long menuId);


}
