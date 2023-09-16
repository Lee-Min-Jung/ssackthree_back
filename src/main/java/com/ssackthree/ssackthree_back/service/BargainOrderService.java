package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.BargainStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final NotificationService notificationService;



    // 흥정 주문
    public void bargainOrder(BargainOrderRequestDto bargainOrderRequestDto){
        BargainOrderEntity bargainOrderEntity = BargainOrderEntity.builder()
                .menuEntity(menuRepository.findById(bargainOrderRequestDto.getMenuId()).get())
                .userEntity(userRepository.findById(bargainOrderRequestDto.getProposerUserId()).get())
                .bargainPrice(bargainOrderRequestDto.getBargainPrice())
                .status(BargainStatusEnum.BARGAIN_ACTIVE)
                .createdDate(LocalDateTime.now())
                .build();
        bargainOrderRepository.save(bargainOrderEntity);


        // 점주에게 흥정 공지
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                .title("흥정 제안")
                .content(bargainOrderEntity.getMenuEntity().getName()+"에 " + bargainOrderEntity.getBargainPrice() + "원으로 흥정이 들어왔습니다.")
                .createdDate(String.valueOf(LocalDateTime.now()))
                .build();
        notificationService.notify(bargainOrderRequestDto.getReceiverUserId(), notificationResponseDto);
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

        // 흥정 실패 알림 보내기
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                .title("흥정 실패")
                .content(menuRepository.findById(bargainDenyRequestDto.getMenuId()).get().getName() + " 흥정에 실패했습니다")
                .createdDate(String.valueOf(LocalDateTime.now()))
                .build();
        notificationService.notify(bargainDenyRequestDto.getUserId(), notificationResponseDto);

    }
    // 흥정 수락
    public void acceptBargain(BargainAcceptDenyRequestDto bargainAcceptRequestDto){
        // 흥정 수락된 메뉴에 흥정 제안한 다른 흥정 제안의 흥정 주문 상태 바꾸기
        Optional<List<BargainOrderEntity>> bargainOrderEntityList = bargainOrderRepository.findByMenuEntityId(bargainAcceptRequestDto.getMenuId());
        if(bargainOrderEntityList.isPresent()){
            for(BargainOrderEntity bargainOrder : bargainOrderEntityList.get()){
                if(bargainAcceptRequestDto.getUserId() != bargainOrder.getUserEntity().getId()){
                    BargainOrderEntity bargainOrderEntity = BargainOrderEntity.builder()
                            .id(bargainOrder.getId())
                            .menuEntity(bargainOrder.getMenuEntity())
                            .userEntity(bargainOrder.getUserEntity())
                            .bargainPrice(bargainOrder.getBargainPrice())
                            .status(BargainStatusEnum.BARGAIN_FAIL)
                            .modifiedDate(LocalDateTime.now())
                            .createdDate(bargainOrder.getCreatedDate())
                            .build();
                    bargainOrderRepository.save(bargainOrderEntity);
                    // 알림
                    NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                            .title("흥정 실패")
                            .content(bargainOrderEntity.getMenuEntity().getName() + " 흥정에 실패했습니다")
                            .createdDate(String.valueOf(LocalDateTime.now()))
                            .build();
                    notificationService.notify(bargainOrderEntity.getUserEntity().getId(), notificationResponseDto);
                }

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

        // 수락받은 유저에게 알림 보내기
        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder()
                .title("흥정 성공")
                .content(menuRepository.findById(bargainAcceptRequestDto.getMenuId()).get().getName() + " 흥정에 성공했습니다")
                .createdDate(String.valueOf(LocalDateTime.now()))
                .build();
        notificationService.notify(bargainAcceptRequestDto.getUserId(), notificationResponseDto);
    }

    // 흥정 주문 상태 바꾸기
    public void changeBargainOrderStatus(BargainAcceptDenyRequestDto bargainAcceptDenyRequestDto, BargainStatusEnum bargainStatusEnum){
        Optional<BargainOrderEntity> bargainOrderEntity = bargainOrderRepository.findByMenuEntityIdAndUserEntityId(bargainAcceptDenyRequestDto.getMenuId(), bargainAcceptDenyRequestDto.getUserId());
        if(bargainOrderEntity.isPresent()){
            BargainOrderEntity bargainOrder = BargainOrderEntity.builder()
                    .id(bargainOrderEntity.get().getId())
                    .bargainPrice(bargainOrderEntity.get().getBargainPrice())
                    .status(bargainStatusEnum)
                    .menuEntity(bargainOrderEntity.get().getMenuEntity())
                    .userEntity(bargainOrderEntity.get().getUserEntity())
                    .modifiedDate(LocalDateTime.now())
                    .createdDate(bargainOrderEntity.get().getCreatedDate())
                    .build();
            bargainOrderRepository.save(bargainOrder);
        }
    }
}
