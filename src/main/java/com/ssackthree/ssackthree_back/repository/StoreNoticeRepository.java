package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.StoreNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreNoticeRepository extends JpaRepository<StoreNoticeEntity, Long> {
    Optional<List<StoreNoticeEntity>> findAllByUserEntityId(long id);
}
