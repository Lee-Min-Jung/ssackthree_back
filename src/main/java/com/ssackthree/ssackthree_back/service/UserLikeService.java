package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.UserMenuLikeRequestDto;
import com.ssackthree.ssackthree_back.dto.UserStoreLikeRequestDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserLikeService {
    private final UserMenuLikeRepository userMenuLikeRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final UserStoreLikeRepository userStoreLikeRepository;


    // 메뉴 좋아요
    public void likeMenu(UserMenuLikeRequestDto userMenuLikeRequestDto){
        // user 찾기
        Optional<UserEntity> user = userRepository.findById(userMenuLikeRequestDto.getUserId());

        // 메뉴 찾기
        Optional<MenuEntity> menu = menuRepository.findById(userMenuLikeRequestDto.getMenuId());

        // userLike 생성 및 저장
        UserMenuLikeEntity userMenuLikeEntity = UserMenuLikeEntity.builder()
                .userEntity(user.get())
                .menuEntity(menu.get())
                .build();

        userMenuLikeRepository.save(userMenuLikeEntity);
    }


    // 메뉴 좋아요 취소
    public void unlikeMenu(UserMenuLikeRequestDto userLikeRequestDto){
        userMenuLikeRepository.deleteByUserEntityIdAndMenuEntityId(userLikeRequestDto.getUserId(), userLikeRequestDto.getMenuId());
    }

    // 가게 좋아요
    public void likeStore(UserStoreLikeRequestDto userStoreLikeRequestDto){
        // user 찾기
        Optional<UserEntity> user = userRepository.findById(userStoreLikeRequestDto.getUserId());

        // 메뉴 찾기
        Optional<StoreEntity> store = storeRepository.findById(userStoreLikeRequestDto.getStoreId());

        // userLike 생성 및 저장
        UserStoreLikeEntity userStoreLikeEntity = UserStoreLikeEntity.builder()
                .userEntity(user.get())
                .storeEntity(store.get())
                .build();

        userStoreLikeRepository.save(userStoreLikeEntity);
    }

    // 가게 좋아요 취소
    public void unlikeStore(UserStoreLikeRequestDto userStoreLikeRequestDto){
        userStoreLikeRepository.deleteByUserEntityIdAndStoreEntityId(userStoreLikeRequestDto.getUserId(), userStoreLikeRequestDto.getStoreId());
    }
}
