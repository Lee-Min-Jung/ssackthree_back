package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomerMpReviewResponseDto {
    private String menuName;
    private String storeName;
    private String content;
    private int score;
    private String createdDate;
    private String menuImagePath;
}
