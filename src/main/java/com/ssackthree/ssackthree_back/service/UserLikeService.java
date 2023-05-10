package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.UserLikeRequestDto;
import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.entity.UserLikeEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.UserLikeRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserLikeService {
    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public void likeMenu(UserLikeRequestDto userLikeRequestDto){
        // user 찾기
        Optional<UserEntity> user = userRepository.findById(userLikeRequestDto.getUserId());

        // 메뉴 찾기
        Optional<MenuEntity> menu = menuRepository.findById(userLikeRequestDto.getMenuId());

        // userLike 생성 및 저장
        UserLikeEntity userLikeEntity = UserLikeEntity.builder()
                .userEntity(user.get())
                .menuEntity(menu.get())
                .build();

        userLikeRepository.save(userLikeEntity);
    }

    public void unlikeMenu(UserLikeRequestDto userLikeRequestDto){
        userLikeRepository.deleteByUserEntityIdAndMenuEntityId(userLikeRequestDto.getUserId(), userLikeRequestDto.getMenuId());
    }
}
