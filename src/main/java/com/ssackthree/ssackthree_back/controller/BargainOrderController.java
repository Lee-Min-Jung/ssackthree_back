package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.BargainAcceptDenyRequestDto;
import com.ssackthree.ssackthree_back.dto.BargainListResponseDto;
import com.ssackthree.ssackthree_back.dto.BargainOrderRequestDto;
import com.ssackthree.ssackthree_back.dto.BeforeOrderResponseDto;
import com.ssackthree.ssackthree_back.service.BargainOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/bargain")
public class BargainOrderController {

    private final BargainOrderService bargainOrderService;

    @PostMapping("/order")
    public int bargainOrder(@RequestBody BargainOrderRequestDto bargainOrderRequestDto){
        bargainOrderService.bargainOrder(bargainOrderRequestDto);
        return 1;
    }

    @GetMapping("/list/{menuId}")
    public List<BargainListResponseDto> bargainOrder(@PathVariable(name = "menuId") long menuId){
        return bargainOrderService.bargainList(menuId);
    }

    @GetMapping("/before/{menuId}")
    public BeforeOrderResponseDto maxBargainPrice(@PathVariable(name = "menuId") long menuId){
        return bargainOrderService.beforeOrder(menuId);
    }

    @PostMapping("/accept")
    public int acceptBargain(@RequestBody BargainAcceptDenyRequestDto bargainAcceptRequestDto){
        bargainOrderService.acceptBargain(bargainAcceptRequestDto);
        return 1;
    }

    @PostMapping("/deny")
    public int denyBargain(@RequestBody BargainAcceptDenyRequestDto bargainDenyRequestDto){
        bargainOrderService.denyBargain(bargainDenyRequestDto);
        return 1;
    }
}
