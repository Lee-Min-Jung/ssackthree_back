package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MyTownProductHopingPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTownProductHopingPlaceRepository extends JpaRepository<MyTownProductHopingPlaceEntity, Long> {
}
