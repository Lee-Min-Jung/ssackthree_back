package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.HomePageRequestDto;
import com.ssackthree.ssackthree_back.dto.MenuDetailResponseDto;
import com.ssackthree.ssackthree_back.dto.MenuInDistanceResponseDto;
import com.ssackthree.ssackthree_back.dto.MenuRegisterRequestDto;
import com.ssackthree.ssackthree_back.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/menu")
public class MenuController {

    private final MenuService menuService;

    @PostMapping(path = "register")
    public void registerMenu(@RequestPart(value="dto") MenuRegisterRequestDto menuRegisterRequestDto, @RequestPart(value="menus", required = false) MultipartFile[] menus){
        menuService.registerMenu(menuRegisterRequestDto, menus);
    }

    @GetMapping(path = "get/distance")
    public List<MenuInDistanceResponseDto> getMenuListInDistance(@RequestBody HomePageRequestDto homePageRequestDto){
        return menuService.getMenuListInDistance(homePageRequestDto);
    }

    @GetMapping(path = "get/detail/{menuId}")
    public MenuDetailResponseDto getMenuDetail(@PathVariable(name="menuId") long menuId){
        return menuService.getMenuDetail(menuId);
    }

}
