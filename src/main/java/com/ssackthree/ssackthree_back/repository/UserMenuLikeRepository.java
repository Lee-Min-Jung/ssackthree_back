package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.UserMenuLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMenuLikeRepository extends JpaRepository<UserMenuLikeEntity, Long> {

    void deleteByUserEntityIdAndMenuEntityId(long userId, long menuId);

}
