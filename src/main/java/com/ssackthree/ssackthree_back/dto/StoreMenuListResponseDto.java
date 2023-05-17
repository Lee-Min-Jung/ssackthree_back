package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreMenuListResponseDto {
    String name;
    int originalPrice;
    int discountedPrice;
    String imageUrl;
    int proposerCount;
    long menuId;
}
