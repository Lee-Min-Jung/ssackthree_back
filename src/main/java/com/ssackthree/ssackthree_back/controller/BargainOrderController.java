package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.BargainOrderRequestDto;
import com.ssackthree.ssackthree_back.service.BargainOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/bargain")
public class BargainOrderController {

    private final BargainOrderService bargainOrderService;

    @PostMapping("/order")
    public void bargainOrder(@RequestBody BargainOrderRequestDto bargainOrderRequestDto){
        bargainOrderService.bargainOrder(bargainOrderRequestDto);
    }
}
