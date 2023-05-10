package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.StoreRegisterRequestDto;
import com.ssackthree.ssackthree_back.service.StoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StoreControllerTest {
    @Autowired
    private StoreService storeService;

//    @Test
//    @DisplayName("가게 등록 테스트")
//    public void 가게등록() throws Exception{
//        //Given
//        StoreRegisterRequestDto storeRegisterRequestDto = StoreRegisterRequestDto.builder()
//                .mainAddress("경기도")
//                .detailAddress("수원시")
//                .endTime("21")
//                .startTime("9")
//                .holiday("월")
//                .introduce("환영합니다.")
//                .phoneNumber("01012345678")
//                .zipcode("12345")
//                .build();
//        //when
////        String result = storeService.register(storeRegisterRequestDto);
//
//        //then
////        Assertions.assertThat(result).isEqualTo("success");
//    }
}