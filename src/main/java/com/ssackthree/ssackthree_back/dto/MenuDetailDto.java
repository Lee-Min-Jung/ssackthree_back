package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDetailDto {

    private String name;
    private String isBargainning;
    private int bargainLimitTime;
    private LocalDateTime saleEndTime;
    private int originalPrice;
    private int discountedPrice;
    private String imagePath;
}
