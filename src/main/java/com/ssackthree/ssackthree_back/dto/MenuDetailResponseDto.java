package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDetailResponseDto {

    private MenuDetailDto menuDetail;
    private List<MenuOtherDto> menuOther = new ArrayList<>();
    private MenuStoreDto menuStore;
}
