package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.BargainOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface BargainOrderRepository extends JpaRepository<BargainOrderEntity, Long> {

    Optional<List<BargainOrderEntity>> findByMenuEntityId(long menuId);
}