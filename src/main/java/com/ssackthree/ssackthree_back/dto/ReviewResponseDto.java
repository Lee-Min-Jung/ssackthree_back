package com.ssackthree.ssackthree_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReviewResponseDto {
    private long writerId;
    private String writerName;
    private LocalDateTime createdDate;
    private int score;
    private String content;
    private String menuName;
    private String writerProfileImagePath;
}
