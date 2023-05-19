package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<List<OrderEntity>> findAllById(long userId);
}
