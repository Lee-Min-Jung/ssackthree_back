package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.JoinRequestDto;
import com.ssackthree.ssackthree_back.dto.UserLikeRequestDto;
import com.ssackthree.ssackthree_back.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/customer/like")
public class UserLikeController {

    private final UserLikeService userLikeService;

    @PostMapping(path = "/menu")
    public void likeMenu(@RequestBody UserLikeRequestDto userLikeRequestDto) throws Exception{
        userLikeService.likeMenu(userLikeRequestDto);
    }

    @DeleteMapping(path = "/menu")
    public void unlikeMenu(@RequestBody UserLikeRequestDto userLikeRequestDto) throws Exception{
        userLikeService.unlikeMenu(userLikeRequestDto);
    }
}
