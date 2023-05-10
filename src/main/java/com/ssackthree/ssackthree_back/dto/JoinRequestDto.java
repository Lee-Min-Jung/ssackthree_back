package com.ssackthree.ssackthree_back.dto;

import com.ssackthree.ssackthree_back.enums.RoleEnum;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDto {

    private String username;
    private String password;
    private String repName;
    private String role;
}
