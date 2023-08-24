package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.StoreRegisterRequestDto;
import com.ssackthree.ssackthree_back.dto.StoreRegisterResponseDto;
import com.ssackthree.ssackthree_back.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping(path = "register")
    public int registerStore(@RequestPart(value="dto") StoreRegisterRequestDto storeRegisterRequestDto, @RequestPart(value="profile", required = false) MultipartFile profile, @RequestPart(value="menus", required = false) MultipartFile[] menus) throws Exception{
        storeService.registerStore(storeRegisterRequestDto, profile, menus);
        return 1;
    }

//    @PostMapping(path = "update")
//    public int updateStore(@RequestPart(value="dto") StoreRegisterRequestDto storeRegisterRequestDto, @RequestPart(value="profile", required = false) MultipartFile profile, @RequestPart(value="menus", required = false) MultipartFile[] menus) throws Exception{
//        storeService.updateStore(storeRegisterRequestDto, profile, menus);
//        return 1;
//    }
//
//    @GetMapping(path = "get/{userId}")
//    public StoreRegisterResponseDto getStore(@PathVariable(name="userId") long userId) throws Exception{
//        return storeService.getStore(userId);
//    }
//
//    @GetMapping(path = "/profile/show/{userId}")
//    public String getProfile(@PathVariable(name="userId") long userId) throws Exception{
//        return storeService.getProfile(userId);
//    }

}
