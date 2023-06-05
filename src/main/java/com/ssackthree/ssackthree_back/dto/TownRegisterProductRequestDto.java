package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TownRegisterProductRequestDto {
    private String title;
    private int price;
    private String content;
    private String hopingPlaceAddress;
    private long userId;
}
