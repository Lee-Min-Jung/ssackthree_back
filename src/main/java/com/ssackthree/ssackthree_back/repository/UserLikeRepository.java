package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.entity.UserLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLikeEntity, Long> {

    void deleteByUserEntityIdAndMenuEntityId(long userId, long menuId);

}
