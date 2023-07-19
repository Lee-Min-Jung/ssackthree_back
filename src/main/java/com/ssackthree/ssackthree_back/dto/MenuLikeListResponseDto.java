package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuLikeListResponseDto {
    private String menuName;
    private long menuId;
    private String storeName;
    private int originalPrice;
    private int discountedPrice;
    private String isBargain;
    private String menuImagePath;
}
