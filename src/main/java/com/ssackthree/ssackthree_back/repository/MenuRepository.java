package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    @Query("SELECT m.id " +
            "FROM MenuEntity m " +
            "WHERE (:isBargainning = '' OR m.isBargainning = :isBargainning) " +
            "AND (:typeList IS NULL OR m.type IN :typeList)" +
            "AND (m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.ORDER_ING OR m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.BARGAIN_ING OR m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.BARGAIN_SUCCESS)"
    )
    List<Long> findIdsByIsBargainningAndTypeIn(@Param("isBargainning") String isBargainning, @Param("typeList") List<MenuTypeEnum> typeList);

    @Query("select m " +
            "from MenuEntity m " +
            "where (m.isBargainning = :isBargainning) " +
            "and (m.storeEntity.id = :storeId) " +
            "and (m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.ORDER_ING OR m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.BARGAIN_ING OR m.menuStatusEntity.menuStatus = com.ssackthree.ssackthree_back.enums.MenuStatusEnum.BARGAIN_SUCCESS)")
    Optional<List<MenuEntity>> findByStoreEntityIdAndIsBargainningAndMenuStatus(long storeId, @Param("isBargainning") String isBargainning);

//    @Query("select m from MenuEntity m where (m.name LIKE :searchWord)")
//    Optional<List<MenuEntity>> findByWord(@Param("searchWord") String searchWord);



}
