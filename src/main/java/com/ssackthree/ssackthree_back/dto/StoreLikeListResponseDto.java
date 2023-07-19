package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreLikeListResponseDto {
    private String storeName;
    private long storeId;
    private String storeProfileImagePath;
    private String address;
}
