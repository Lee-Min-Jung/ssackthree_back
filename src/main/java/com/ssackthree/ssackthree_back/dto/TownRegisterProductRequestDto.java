package com.ssackthree.ssackthree_back.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TownRegisterProductRequestDto {
    private long userId;
    private String title;
    private int price;
    private String content;
    private String hopingPlaceAddress;
    private List<MultipartFile> productImages;
}
