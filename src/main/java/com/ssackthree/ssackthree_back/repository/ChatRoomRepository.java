package com.ssackthree.ssackthree_back.repository;

import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {


    @Query("SELECT cr FROM ChatRoomEntity cr WHERE (cr.userEntity1.id = :id1 AND cr.userEntity2.id = :id2) OR (cr.userEntity1.id = :id2 AND cr.userEntity2.id = :id1)")
    Optional<ChatRoomEntity> findByUserEntity1IdAndUserEntity2Id(@Param("id1") long id1, @Param("id2") long id2);
}
