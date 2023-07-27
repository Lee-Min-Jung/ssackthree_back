package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.service.MyTownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/town")
public class MyTownController {

    private final MyTownService myTownService;

    @PostMapping("register")
    public int registerProduct(TownRegisterProductRequestDto townRegisterProductRequestDto) throws Exception {
        log.info("registerProduct 컨트롤러 실행");
        return myTownService.registerProduct(townRegisterProductRequestDto);
    }

    @PostMapping("list")
    public List<TownProductResponseDto> getTownProductList(@RequestBody TownHomeRequestDto townHomeRequestDto){
        return myTownService.getTownProductList(townHomeRequestDto);
    }

    @GetMapping("list/detail/{productId}/{userId}")
    public TownProductDetailResponseDto getTownProductDetail(@PathVariable(name = "productId") long productId, @PathVariable(name = "userId") long userId){
        return myTownService.getTownProductDetail(productId, userId);
    }

    @PostMapping("sell/list")
    public List<TownProductResponseDto> getTownProductSellList(@RequestBody TownSellListRequestDto townSellListRequestDto){
        return myTownService.getTownSellList(townSellListRequestDto);
    }

    @PostMapping("update/status")
    public int updateTownProductStatus(@RequestBody TownProductStatusUpdateRequestDto townProductStatusUpdateRequestDto){
        myTownService.updateTownProductStats(townProductStatusUpdateRequestDto);
        return 1;
    }
}
