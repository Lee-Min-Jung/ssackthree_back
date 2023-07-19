package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.StoreLikeListResponseDto;
import com.ssackthree.ssackthree_back.dto.UserMenuLikeRequestDto;
import com.ssackthree.ssackthree_back.dto.UserStoreLikeRequestDto;
import com.ssackthree.ssackthree_back.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/customer/like")
public class UserLikeController {

    private final UserLikeService userLikeService;

    @PostMapping(path = "/menu")
    public int likeMenu(@RequestBody UserMenuLikeRequestDto userLikeRequestDto) throws Exception{
        userLikeService.likeMenu(userLikeRequestDto);
        return 1;
    }

    @DeleteMapping(path = "/menu")
    public int unlikeMenu(@RequestBody UserMenuLikeRequestDto userLikeRequestDto) throws Exception{
        userLikeService.unlikeMenu(userLikeRequestDto);
        return 1;
    }

    @PostMapping(path = "/store")
    public int likeStore(@RequestBody UserStoreLikeRequestDto userStoreLikeRequestDto) throws Exception{
        userLikeService.likeStore(userStoreLikeRequestDto);
        return 1;
    }

    @DeleteMapping(path = "/store")
    public int unlikeStore(@RequestBody UserStoreLikeRequestDto userStoreLikeRequestDto) throws Exception{
        userLikeService.unlikeStore(userStoreLikeRequestDto);
        return 1;
    }

    @GetMapping(path = "/store/list/{userId}")
    public List<StoreLikeListResponseDto> storeLikeList(@PathVariable(name = "userId") long userId){
        return userLikeService.storeLikeList(userId);
    }


}
