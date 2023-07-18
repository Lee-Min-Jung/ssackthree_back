package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.ReviewRequestDto;
import com.ssackthree.ssackthree_back.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(path = "/register")
    public int registerReview(@RequestBody ReviewRequestDto reviewRequestDto){
        reviewService.registerReview(reviewRequestDto);
        return 1;
    }
}
