package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeforeOrderResponseDto {
    String menuName;
    String saleEndTime;
    int menuPrice;
    String bargainEndTime;
    int maxBargainPrice;
}
