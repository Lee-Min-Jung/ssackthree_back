package com.ssackthree.ssackthree_back.dto;

import lombok.*;
import org.apache.catalina.Store;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuInDistanceResponseDto {

    private long menuId;
    private String name;
    private int originalPrice;
    private int discountedPrice;
    private double distance;
    private String storeName;
    private String menuImagePath;
    private int likeCount;
    private LocalDateTime createdDate;

}
