package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.MenuLikeListResponseDto;
import com.ssackthree.ssackthree_back.dto.StoreLikeListResponseDto;
import com.ssackthree.ssackthree_back.dto.UserMenuLikeRequestDto;
import com.ssackthree.ssackthree_back.dto.UserStoreLikeRequestDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    // 메뉴 좋아요 리스트
    public List<MenuLikeListResponseDto> menuLikeList(long userId){
        Optional<UserEntity> user = userRepository.findById(userId);
        List<MenuLikeListResponseDto> menuLikeListResponseDtoList = new ArrayList<>();

        for(UserMenuLikeEntity ul : user.get().getUserMenuLikeEntityList()){
            MenuLikeListResponseDto menuLikeListResponseDto = MenuLikeListResponseDto.builder()
                    .menuName(ul.getMenuEntity().getName())
                    .menuId(ul.getMenuEntity().getId())
                    .menuImagePath(ul.getMenuEntity().getMenuFileEntity().get(0).getFilePath())
                    .originalPrice(ul.getMenuEntity().getOriginalPrice())
                    .discountedPrice(ul.getMenuEntity().getDiscountedPrice())
                    .isBargain(ul.getMenuEntity().getIsBargainning())
                    .storeName(ul.getMenuEntity().getStoreEntity().getStoreName())
                    .build();
            menuLikeListResponseDtoList.add(menuLikeListResponseDto);


        }

        return menuLikeListResponseDtoList;
    }

    // 가게 좋아요 리스트
    public List<StoreLikeListResponseDto> storeLikeList(long userId){
        Optional<UserEntity> user = userRepository.findById(userId);
        List<StoreLikeListResponseDto> storeLikeListResponseDtoList = new ArrayList<>();

        for(UserStoreLikeEntity ul : user.get().getUserStoreLikeEntityList()){
            StoreLikeListResponseDto storeLikeListResponseDto = StoreLikeListResponseDto.builder()
                    .storeName(ul.getStoreEntity().getStoreName())
                    .storeId(ul.getStoreEntity().getId())
                    .storeProfileImagePath(ul.getStoreEntity().getStoreProfileFileEntity().getFilePath())
                    .address(ul.getStoreEntity().getMainAddress()+ul.getStoreEntity().getDetailAddress())
                    .build();
            storeLikeListResponseDtoList.add(storeLikeListResponseDto);
        }

        return storeLikeListResponseDtoList;
    }
}
