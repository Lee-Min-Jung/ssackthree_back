package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MyTownProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTownProductRepository extends JpaRepository<MyTownProductEntity, Long> {
}
