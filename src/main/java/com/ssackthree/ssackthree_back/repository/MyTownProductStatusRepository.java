package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MyTownProductStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTownProductStatusRepository extends JpaRepository<MyTownProductStatusEntity, Long> {
    void deleteByMyTownProductEntityId(long id);
}
