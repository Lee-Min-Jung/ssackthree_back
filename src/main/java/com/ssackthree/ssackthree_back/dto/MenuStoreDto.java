package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuStoreDto {
    private long storeId;
    private String storeName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String holiday;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private String mainAddress;
    private String detailAddress;
    private String storeImagePath;
    private String isStoreLike;
}
