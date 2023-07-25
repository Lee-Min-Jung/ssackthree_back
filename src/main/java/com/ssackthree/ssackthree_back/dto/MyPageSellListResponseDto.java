package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageSellListResponseDto {
    private String menuName;
    private int originalPrice;
    private int discountedPrice;
    private String isBargain;
    private String imagePath;
    private String buyer;
    private String date;
}
