package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuStoreDto {
    private String storeName;
    private int startTime;
    private int endTime;
    private String holiday;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private String mainAddress;
    private String detailAddress;
    private String storeImagePath;
}
