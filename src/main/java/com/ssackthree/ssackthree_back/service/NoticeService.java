package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.entity.StoreNoticeEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.StoreNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final StoreNoticeRepository storeNoticeRepository;

    public void sendBargainNoticeToStore(UserEntity user, MenuEntity menu, int bargainPrice){
        // 흥정 내용 추출
        String proposer = user.getRepName();
        String menuName = menu.getName();

        StoreNoticeEntity storeNoticeEntity = StoreNoticeEntity.builder()
                .menuEntity(menu)
                .userEntity(user)
                .title("흥정 제안")
                .content(proposer + "님이 " + menuName + "에 " + String.valueOf(bargainPrice) + "원을 제안했습니다.")
                .createdDate(LocalDateTime.now())
                .build();

        storeNoticeRepository.save(storeNoticeEntity);
    }
}
