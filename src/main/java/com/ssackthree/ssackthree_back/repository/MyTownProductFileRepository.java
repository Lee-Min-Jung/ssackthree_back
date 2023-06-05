package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MyTownProductFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTownProductFileRepository extends JpaRepository<MyTownProductFileEntity, Long> {
}
