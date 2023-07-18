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
    long menuId;
    String storeName;
    long storeId;
    int originalPrice;
    int discountedPrice;
    String status;
    String date;
}
