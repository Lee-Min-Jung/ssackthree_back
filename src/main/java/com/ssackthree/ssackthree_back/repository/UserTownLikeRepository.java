package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.UserTownLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTownLikeRepository extends JpaRepository<UserTownLikeEntity, Long> {
    void deleteByUserEntityIdAndMyTownProductEntityId(long userId, long townId);
}
