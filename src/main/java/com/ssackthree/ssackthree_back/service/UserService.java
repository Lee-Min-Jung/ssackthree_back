package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.config.jwt.JwtProvider;
import com.ssackthree.ssackthree_back.dto.CustomerNicknameRequestDto;
import com.ssackthree.ssackthree_back.dto.JoinRequestDto;
import com.ssackthree.ssackthree_back.dto.LoginRequestDto;
import com.ssackthree.ssackthree_back.dto.TokenResponseDto;
import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.StoreEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.enums.RoleEnum;
import com.ssackthree.ssackthree_back.repository.StoreRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;



    @Value("${upload-path}")
    private String uploadPath;

    public void join(JoinRequestDto joinRequestDto) throws Exception{
        RoleEnum role = joinRequestDto.getRole().equals("customer") ? RoleEnum.ROLE_CUSTOMER : RoleEnum.ROLE_STORE;

        UserEntity userEntity = UserEntity.builder()
                .username(joinRequestDto.getUsername())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .role(role)
                .repName(joinRequestDto.getRepName())
                .build();

        userRepository.save(userEntity);

    }

    public TokenResponseDto login(LoginRequestDto loginRequestDto) throws Exception{
        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        return jwtProvider.generateToken(user);
    }



//    public String setNickname(CustomerNicknameRequestDto customerNicknameRequestDto) throws Exception{
//        long id = customerNicknameRequestDto.getUserId();
//        Optional<UserEntity> user = userRepository.findById(id);
//        if(user.isPresent()){
//            UserEntity userEntity = UserEntity.builder()
//                    .id(id)
//                    .repName(customerNicknameRequestDto.getNickname())
//                    .password(user.get().getPassword())
//                    .username(user.get().getUsername())
//                    .role(user.get().getRole())
//                    .build();
//            userRepository.save(userEntity);
//        }
//        return "success";
//    }
}
