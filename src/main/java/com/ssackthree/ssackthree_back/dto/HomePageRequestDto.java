package com.ssackthree.ssackthree_back.dto;

import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomePageRequestDto {

    private double latitude;
    private double longitude;
    private int km;
    private String sortType;
    private String isBargainning;
    private List<MenuTypeEnum> typeList;

}
