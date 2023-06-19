package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

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
    private String type;
    private String endTime;
    private List<MultipartFile> menuImages;
    private int limitTime;
    private int minPrice;

}
