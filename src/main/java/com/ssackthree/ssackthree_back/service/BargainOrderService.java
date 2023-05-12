package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.BargainOrderRequestDto;
import com.ssackthree.ssackthree_back.entity.BargainOrderEntity;
import com.ssackthree.ssackthree_back.repository.BargainOrderRepository;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BargainOrderService {

    private final BargainOrderRepository bargainOrderRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public void bargainOrder(BargainOrderRequestDto bargainOrderRequestDto){
        BargainOrderEntity bargainOrderEntity = BargainOrderEntity.builder()
                .menuEntity(menuRepository.findById(bargainOrderRequestDto.getMenuId()).get())
                .userEntity(userRepository.findById(bargainOrderRequestDto.getUserId()).get())
                .bargainPrice(bargainOrderRequestDto.getBargainPrice())
                .status("A")
                .build();
        bargainOrderRepository.save(bargainOrderEntity);
    }
}
