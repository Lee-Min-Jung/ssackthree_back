package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderBargainHistoryResponseDto {
    String menuName;
    String storeName;
    int originalPrice;
    int discountedPrice;
    String status;
    String date;
}
