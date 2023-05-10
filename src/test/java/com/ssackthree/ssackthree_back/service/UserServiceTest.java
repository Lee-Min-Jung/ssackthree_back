package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.CustomerNicknameRequestDto;
import com.ssackthree.ssackthree_back.dto.JoinRequestDto;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.enums.RoleEnum;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;


    @Test
    @DisplayName("회원가입 테스트")
    public void 회원가입() throws Exception{
        //Given
        JoinRequestDto joinRequestDto = JoinRequestDto.builder()
                .username("lmj")
                .password("123")
                .role("customer")
                .repName("닉네임")
                .build();

        //when
//        String result = userService.join(joinRequestDto);
//
//        //then
//        Assertions.assertThat(result).isEqualTo("success");
    }

    @Test
    @DisplayName("손님 닉네임 설정 테스트")
    public void 닉네임설정() throws Exception{
        //Given
        CustomerNicknameRequestDto customerNicknameRequestDto = CustomerNicknameRequestDto.builder()
                .userId(2)
                .nickname("lmj")
                .build();
        //when
//        String result = userService.setNickname(customerNicknameRequestDto);

        //then
//        Assertions.assertThat(result).isEqualTo("success");
    }
}