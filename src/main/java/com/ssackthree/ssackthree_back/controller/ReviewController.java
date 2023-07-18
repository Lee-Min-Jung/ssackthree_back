package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.ReviewRequestDto;
import com.ssackthree.ssackthree_back.dto.ReviewResponseDto;
import com.ssackthree.ssackthree_back.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "api/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping(path = "/register")
    public int registerReview(@RequestBody ReviewRequestDto reviewRequestDto){
        reviewService.registerReview(reviewRequestDto);
        return 1;
    }

    // 리뷰 보기
    @GetMapping(path = "/show/{storeId}")
    public List<ReviewResponseDto> showReview(@PathVariable(name = "storeId") long storeId){
        return reviewService.showReview(storeId);
    }
}
