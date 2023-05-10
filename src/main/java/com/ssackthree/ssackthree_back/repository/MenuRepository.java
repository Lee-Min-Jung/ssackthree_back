package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    @Query("SELECT m.id FROM MenuEntity m WHERE (:isBargainning = '' OR m.isBargainning = :isBargainning) AND (:typeList IS NULL OR m.type IN :typeList)")
    List<Long> findIdsByIsBargainningAndTypeIn(@Param("isBargainning") String isBargainning, @Param("typeList") List<MenuTypeEnum> typeList);




}
