package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.MenuRegisterRequestDto;
import com.ssackthree.ssackthree_back.dto.TownRegisterProductRequestDto;
import com.ssackthree.ssackthree_back.service.MyTownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/town")
public class MyTownController {

    private final MyTownService myTownService;

    @PostMapping("register")
    public int registerProduct(@RequestPart(value="dto") TownRegisterProductRequestDto townRegisterProductRequestDto, @RequestPart(value="products", required = false) MultipartFile[] products) throws Exception {
        log.info("registerProduct 컨트롤러");
        return myTownService.registerProduct(townRegisterProductRequestDto, products);
    }
}
