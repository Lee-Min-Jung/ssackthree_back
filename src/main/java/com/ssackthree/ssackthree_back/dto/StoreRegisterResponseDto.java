package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreRegisterResponseDto{
    private long id;
    private String storeName;
    private String mainAddress;
    private String detailAddress;
    private String zipcode;
    private String phoneNumber;
    private int startTime;
    private int endTime;
    private String holiday;
    private String introduce;
    private ArrayList<String> menuFileNameList = new ArrayList<>();
}
