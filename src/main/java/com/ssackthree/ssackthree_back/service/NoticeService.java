package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.BargainOrderRequestDto;
import com.ssackthree.ssackthree_back.dto.NoticeResponseDto;
import com.ssackthree.ssackthree_back.entity.StoreNoticeEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.StoreNoticeRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final StoreNoticeRepository storeNoticeRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public void sendBargainNoticeToStore(BargainOrderRequestDto bargainOrderRequestDto){
        // 흥정 내용 추출
        String proposer = userRepository.findById(bargainOrderRequestDto.getProposerUserId()).get().getRepName();
        String menuName = menuRepository.findById(bargainOrderRequestDto.getMenuId()).get().getName();

        StoreNoticeEntity storeNoticeEntity = StoreNoticeEntity.builder()
                .userEntity(userRepository.findById(bargainOrderRequestDto.getReceiverUserId()).get())
                .title("흥정 제안")
                .content(proposer + "님이 " + menuName + "에 " + String.valueOf(bargainOrderRequestDto.getBargainPrice()) + "원을 제안했습니다.")
                .createdDate(LocalDateTime.now())
                .build();

        storeNoticeRepository.save(storeNoticeEntity);
    }

    public List<NoticeResponseDto> getNoticeList(long userId){
        Optional<List<StoreNoticeEntity>> storeNoticeEntityList = storeNoticeRepository.findAllByUserEntityId(userId);
        List<NoticeResponseDto> noticeResponseDtoList = new ArrayList<>();

        if(storeNoticeEntityList.isPresent()){
            for(StoreNoticeEntity storeNoticeEntity : storeNoticeEntityList.get()){
                NoticeResponseDto noticeResponseDto = NoticeResponseDto.builder()
                        .title(storeNoticeEntity.getTitle())
                        .content(storeNoticeEntity.getContent())
                        .createdDate(String.valueOf(storeNoticeEntity.getCreatedDate()))
                        .build();
                noticeResponseDtoList.add(noticeResponseDto);
            }
        }

        return noticeResponseDtoList;

    }
}
