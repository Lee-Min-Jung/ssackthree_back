package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MyTownProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyTownProductRepository extends JpaRepository<MyTownProductEntity, Long> {
    Optional<List<MyTownProductEntity>> findByUserEntityId(long userId);
}
