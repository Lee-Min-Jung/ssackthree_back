package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.BargainOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface BargainOrderRepository extends JpaRepository<BargainOrderEntity, Long> {

    Optional<List<BargainOrderEntity>> findByMenuEntityId(long menuId);

    @Query("select count(bo.id) from BargainOrderEntity bo where bo.menuEntity.storeEntity.id = :storeId and bo.userEntity.id = :userId and bo.status = 'C'")
    int findSuccessTransactionCount(@Param("userId") long userId, @Param("storeId") long storeId);

    @Query("select max(bo.bargainPrice) from BargainOrderEntity bo where bo.menuEntity.id = :menuId")
    Optional<Integer> findMaxBargainPrice(@Param("menuId") long menuId);
}
