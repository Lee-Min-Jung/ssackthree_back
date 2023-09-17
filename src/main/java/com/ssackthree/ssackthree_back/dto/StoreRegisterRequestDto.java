package com.ssackthree.ssackthree_back.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class StoreRegisterRequestDto {

    private String storeName;
    private String mainAddress;
    private String detailAddress;
    private String zipcode;
    private String phoneNumber;
    private String startTime;
    private String endTime;
    private String holiday;
    private String introduce;
    private long userId;
    private List<MultipartFile> profileImages;
    private List<MultipartFile> menuImages;
}
