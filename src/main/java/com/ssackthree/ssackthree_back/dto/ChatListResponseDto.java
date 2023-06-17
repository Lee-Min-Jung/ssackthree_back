package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatListResponseDto {
    private long chatRoomId;
    private long counterpartUserId;
    private String counterpartName;
    private String counterpartRole;
    private String counterpartProfile;
}
