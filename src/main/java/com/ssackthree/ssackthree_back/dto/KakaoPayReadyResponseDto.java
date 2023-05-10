package com.ssackthree.ssackthree_back.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class KakaoPayReadyResponseDto {
    private String tid;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
}
