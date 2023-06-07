package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.UserStoreLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoreLikeRepository extends JpaRepository<UserStoreLikeEntity, Long> {
    void deleteByUserEntityIdAndStoreEntityId(long userId, long storeId);

}
