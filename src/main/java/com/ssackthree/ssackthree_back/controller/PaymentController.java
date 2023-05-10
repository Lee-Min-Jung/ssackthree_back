package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.KakaoPayApproveResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayReadyResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayRequestDto;
import com.ssackthree.ssackthree_back.service.KakaoPayService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/payment")
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakaopay/ready")
    public KakaoPayReadyResponseDto readyPayment(@RequestBody KakaoPayRequestDto kakaoPayRequestDto) {
        return  kakaoPayService.payReady(kakaoPayRequestDto);
    }

    @GetMapping("/kakaopay/success")
    public KakaoPayApproveResponseDto afterPayRequest(@RequestParam("pg_token") String pgToken, @RequestParam("orderId") String orderId){
        return kakaoPayService.payApprove(pgToken, orderId);
    }
}
