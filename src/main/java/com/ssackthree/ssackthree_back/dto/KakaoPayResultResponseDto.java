package com.ssackthree.ssackthree_back.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KakaoPayResultResponseDto {
    private int totalPrice;
    private String storeName;
    private String storeAddress;
    private LocalTime endTime;
}
