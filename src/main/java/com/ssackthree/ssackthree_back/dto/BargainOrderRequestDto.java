package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BargainOrderRequestDto {

    private long receiverUserId;
    private long proposerUserId;
    private long menuId;
    private int bargainPrice;
}
