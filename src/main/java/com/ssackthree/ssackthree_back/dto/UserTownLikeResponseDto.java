package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTownLikeResponseDto {
    private String title;
    private String createdDate;
    private String location;
    private String imagePath;
    private int price;
    private String status;
}
