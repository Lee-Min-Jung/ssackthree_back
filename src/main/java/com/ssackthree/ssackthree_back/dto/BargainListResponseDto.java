package com.ssackthree.ssackthree_back.dto;

import com.ssackthree.ssackthree_back.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BargainListResponseDto {
    private int bargainPrice;
    private String proposerNickname;
    private String proposerImageUrl;
    private long userId;
}
