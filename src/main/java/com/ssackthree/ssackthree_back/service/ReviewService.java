package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.ReviewRequestDto;
import com.ssackthree.ssackthree_back.entity.ReviewEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.ReviewRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public void registerReview(ReviewRequestDto reviewRequestDto){
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .content(reviewRequestDto.getContent())
                .score(reviewRequestDto.getScore())
                .createdDate(LocalDateTime.now())
                .userEntity(userRepository.findById(reviewRequestDto.getUserId()).get())
                .menuEntity(menuRepository.findById(reviewRequestDto.getMenuId()).get())
                .build();
        reviewRepository.save(reviewEntity);




    }
}
