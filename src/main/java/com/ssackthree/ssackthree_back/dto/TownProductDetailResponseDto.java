package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TownProductDetailResponseDto {
    private long writerId;
    private long productId;
    private String writerNickname;
    private String title;
    private String status;
    private int price;
    private String hopingPlaceAddress;
    private double latitude;
    private double longitude;
    private String createdDate;
    private String content;
    private String imagePath;
    private String isLike;
    private List<TownOtherProductResponseDto> townOtherProductResponseDtoList = new ArrayList<>();

}
