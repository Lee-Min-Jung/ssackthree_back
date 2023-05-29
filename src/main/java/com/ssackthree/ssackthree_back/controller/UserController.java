package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.CustomerNicknameRequestDto;
import com.ssackthree.ssackthree_back.dto.JoinRequestDto;
import com.ssackthree.ssackthree_back.dto.LoginRequestDto;
import com.ssackthree.ssackthree_back.dto.TokenResponseDto;
import com.ssackthree.ssackthree_back.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping(path = "join")
    public int join(@RequestBody JoinRequestDto joinRequestDto) throws Exception{
        userService.join(joinRequestDto);
        return 1;
    }

    @PostMapping(path = "login")
    public TokenResponseDto login(@RequestBody LoginRequestDto loginRequestDto) throws Exception{
        return userService.login(loginRequestDto);
    }



}
