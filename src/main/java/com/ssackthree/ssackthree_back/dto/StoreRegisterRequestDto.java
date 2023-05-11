package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreRegisterRequestDto {

    private long id;
    private String storeName;
    private String mainAddress;
    private String detailAddress;
    private String zipcode;
    private String phoneNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String holiday;
    private String introduce;
    private long userId;
}
