package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuRegisterRequestDto {

    private long userId;
    private String menuName;
    private int originalPrice;
    private int discountedPrice;
    private String isBargainning;
    private MenuBargainningDto menuBargainningDto;
    private String type;
    private String endTime;

}
