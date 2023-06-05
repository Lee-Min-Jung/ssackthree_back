package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TownProductResponseDto {
    private int price;
    private String title;
    private String hopingPlaceAddress;
    private String createdDate;
    private String imagePath;
    private double distance;
}
