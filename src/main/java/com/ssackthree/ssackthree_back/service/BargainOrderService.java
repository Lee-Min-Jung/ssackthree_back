package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.BargainAcceptDenyRequestDto;
import com.ssackthree.ssackthree_back.dto.BargainListResponseDto;
import com.ssackthree.ssackthree_back.dto.BargainOrderRequestDto;
import com.ssackthree.ssackthree_back.dto.BeforeOrderResponseDto;
import com.ssackthree.ssackthree_back.entity.BargainOrderEntity;
import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.entity.MenuStatusEntity;
import com.ssackthree.ssackthree_back.enums.BargainStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.repository.BargainOrderRepository;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.MenuStatusRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BargainOrderService {

    private final BargainOrderRepository bargainOrderRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final MenuStatusRepository menuStatusRepository;


    // 흥정 주문
    public void bargainOrder(BargainOrderRequestDto bargainOrderRequestDto){
        BargainOrderEntity bargainOrderEntity = BargainOrderEntity.builder()
                .menuEntity(menuRepository.findById(bargainOrderRequestDto.getMenuId()).get())
                .userEntity(userRepository.findById(bargainOrderRequestDto.getUserId()).get())
                .bargainPrice(bargainOrderRequestDto.getBargainPrice())
                .status(BargainStatusEnum.BARGAIN_ACTIVE)
                .build();
        bargainOrderRepository.save(bargainOrderEntity);
    }

    // 메뉴별 흥정 내역
    public List<BargainListResponseDto> bargainList(long menuId){
        Optional<List<BargainOrderEntity>> bargainOrderEntityList = bargainOrderRepository.findByMenuEntityId(menuId);
        List<BargainListResponseDto> bargainListResponseDtoList = new ArrayList<>();

        if(bargainOrderEntityList.isPresent()){
            for(BargainOrderEntity bargainOrder : bargainOrderEntityList.get()){
                // 프로필 이미지 없는 경우 처리
                String userProfileImageUrl = "";
                if(bargainOrder.getUserEntity().getCustomerProfileFileEntity() != null){
                    userProfileImageUrl = bargainOrder.getUserEntity().getCustomerProfileFileEntity().getFilePath();
                }

                int transactionSuccessCount = getTransactionSuccessCount(bargainOrder);

                BargainListResponseDto bargainListResponseDto = BargainListResponseDto.builder()
                        .bargainPrice(bargainOrder.getBargainPrice())
                        .proposerNickname(bargainOrder.getUserEntity().getRepName())
                        .proposerImageUrl(userProfileImageUrl)
                        .transactionSuccessCount(transactionSuccessCount)
                        .userId(bargainOrder.getUserEntity().getId())
                        .build();
                bargainListResponseDtoList.add(bargainListResponseDto);
            }
        }

        return bargainListResponseDtoList;
    }

    // 흥정 거래 성공 횟수 구하기
    public int getTransactionSuccessCount(BargainOrderEntity bargainOrderEntity){
        long userId = bargainOrderEntity.getUserEntity().getId();
        long storeId = bargainOrderEntity.getMenuEntity().getStoreEntity().getId();
        return bargainOrderRepository.findSuccessTransactionCount(userId, storeId);

    }

    //todo 이 부분은 주문과 흥정 공통이기 때문에 order로 묶으면 좋을듯
    // 구매하러가기 눌렀을 때
    public BeforeOrderResponseDto beforeOrder(long menuId){
        Optional<MenuEntity> menu = menuRepository.findById(menuId);
        if(menu.isPresent()){
            // 흥정 처리
            String bargainEndTime = "";
            if(menu.get().getMenuBargainningEntity() != null){
                bargainEndTime = String.valueOf(menu.get().getMenuBargainningEntity().getBargainEnd());
            }
            BeforeOrderResponseDto beforeOrderResponseDto = BeforeOrderResponseDto.builder()
                    .menuName(menu.get().getName())
                    .saleEndTime(menu.get().getEndTime().toString())
                    .menuPrice(menu.get().getDiscountedPrice())
                    .bargainEndTime(bargainEndTime)
                    .maxBargainPrice(getMaxBargainPrice(menuId))
                    .build();
            return beforeOrderResponseDto;
        }
        return null;

    }

    // 메뉴별 흥정 최고가
    public int getMaxBargainPrice(long menuId){
        Optional<Integer> maxBargainPrice = bargainOrderRepository.findMaxBargainPrice(menuId);
        if(maxBargainPrice.isPresent()){
            return maxBargainPrice.get();
        }
        return 0;
    }

    // 흥정 거절
    public void denyBargain(BargainAcceptDenyRequestDto bargainDenyRequestDto){
        // 흥정 거절된 메뉴의 흥정 주문 상태 바꾸기
        changeBargainOrderStatus(bargainDenyRequestDto, BargainStatusEnum.BARGAIN_FAIL);

    }
    // 흥정 수락
    public void acceptBargain(BargainAcceptDenyRequestDto bargainAcceptRequestDto){
        // 흥정 수락된 메뉴에 흥정 제안한 다른 메뉴의 흥정 주문 상태 바꾸기
        Optional<List<BargainOrderEntity>> bargainOrderEntityList = bargainOrderRepository.findByMenuEntityId(bargainAcceptRequestDto.getMenuId());
        if(bargainOrderEntityList.isPresent()){
            for(BargainOrderEntity bargainOrder : bargainOrderEntityList.get()){
                BargainOrderEntity bargainOrderEntity = BargainOrderEntity.builder()
                        .id(bargainOrder.getId())
                        .menuEntity(bargainOrder.getMenuEntity())
                        .userEntity(bargainOrder.getUserEntity())
                        .bargainPrice(bargainOrder.getBargainPrice())
                        .status(BargainStatusEnum.BARGAIN_FAIL)
                        .build();
                bargainOrderRepository.save(bargainOrderEntity);
            }
        }

        // 흥정 수락된 메뉴의 흥정 주문 상태 바꾸기
        changeBargainOrderStatus(bargainAcceptRequestDto, BargainStatusEnum.BARGAIN_SUCCESS);

        // 흥정 수락된 메뉴의 메뉴 상태 바꾸기
        Optional<MenuStatusEntity> menuStatusEntity = menuStatusRepository.findByMenuEntityId(bargainAcceptRequestDto.getMenuId());
        if(menuStatusEntity.isPresent()){
            MenuStatusEntity menuStatus = MenuStatusEntity.builder()
                    .id(menuStatusEntity.get().getId())
                    .menuStatus(MenuStatusEnum.BARGAIN_SUCCESS)
                    .menuEntity(menuStatusEntity.get().getMenuEntity())
                    .build();
            menuStatusRepository.save(menuStatus);
        }
    }

    public void changeBargainOrderStatus(BargainAcceptDenyRequestDto bargainAcceptDenyRequestDto, BargainStatusEnum bargainStatusEnum){
        Optional<BargainOrderEntity> bargainOrderEntity = bargainOrderRepository.findByMenuEntityIdAndUserEntityId(bargainAcceptDenyRequestDto.getMenuId(), bargainAcceptDenyRequestDto.getUserId());
        if(bargainOrderEntity.isPresent()){
            BargainOrderEntity bargainOrder = BargainOrderEntity.builder()
                    .id(bargainOrderEntity.get().getId())
                    .bargainPrice(bargainOrderEntity.get().getBargainPrice())
                    .status(bargainStatusEnum)
                    .menuEntity(bargainOrderEntity.get().getMenuEntity())
                    .userEntity(bargainOrderEntity.get().getUserEntity())
                    .build();
            bargainOrderRepository.save(bargainOrder);
        }
    }
}
