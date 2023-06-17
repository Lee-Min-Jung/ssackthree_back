package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/menu")
public class MenuController {

    private final MenuService menuService;

    @PostMapping(path = "register")
    public int registerMenu(@RequestPart(value="dto") MenuRegisterRequestDto menuRegisterRequestDto, @RequestPart(value="menus", required = false) MultipartFile[] menus) throws IOException {
        menuService.registerMenu(menuRegisterRequestDto, menus);
        return 1;
    }

    @PostMapping(path = "get/distance")
    public List<MenuInDistanceResponseDto> getMenuListInDistance(@RequestBody HomePageRequestDto homePageRequestDto){
        return menuService.getMenuListInDistance(homePageRequestDto);
    }

    // TODO: 2023-06-07 userID를 이렇게 안 보내고 싶은데... home안에 넣어서 보내자니 달랑 그거 하나만 보내는 것 같아서 일단 path로 보냄 나중에 토큰으로 받으면 그걸로 처리하면 좋을 듯
    @GetMapping(path = "get/detail/{menuId}/{userId}")
    public MenuDetailResponseDto getMenuDetail(@PathVariable(name="menuId") long menuId, @PathVariable(name = "userId") long userId){
        return menuService.getMenuDetail(menuId, userId);
    }

    @GetMapping(path = "get/store")
    public List<StoreMenuListResponseDto> getStoreMenuList(@RequestBody StoreMenuListRequestDto storeMenuListRequestDto){
        return menuService.getStoreMenuList(storeMenuListRequestDto);
    }

}
