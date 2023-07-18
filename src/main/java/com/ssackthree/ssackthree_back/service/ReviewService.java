package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.MenuInDistanceResponseDto;
import com.ssackthree.ssackthree_back.dto.ReviewRequestDto;
import com.ssackthree.ssackthree_back.dto.ReviewResponseDto;
import com.ssackthree.ssackthree_back.entity.ReviewEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.ReviewRepository;
import com.ssackthree.ssackthree_back.repository.StoreRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // 리뷰 등록
    public void registerReview(ReviewRequestDto reviewRequestDto){
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .content(reviewRequestDto.getContent())
                .score(reviewRequestDto.getScore())
                .createdDate(LocalDateTime.now())
                .userEntity(userRepository.findById(reviewRequestDto.getUserId()).get())
                .menuEntity(menuRepository.findById(reviewRequestDto.getMenuId()).get())
                .storeEntity(storeRepository.findById(reviewRequestDto.getStoreId()).get())
                .build();
        reviewRepository.save(reviewEntity);

    }

    // 리뷰 보기
    public List<ReviewResponseDto> showReview(long storeId){
        Optional<List<ReviewEntity>> reviewEntityList = reviewRepository.findByStoreEntityId(storeId);
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<ReviewResponseDto>();

        // 리뷰가 존재
        if(reviewEntityList.isPresent()){
           for(ReviewEntity reviewEntity : reviewEntityList.get()){
               ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                       .content(reviewEntity.getContent())
                       .score(reviewEntity.getScore())
                       .menuName(reviewEntity.getMenuEntity().getName())
                       .writerName(reviewEntity.getUserEntity().getRepName())
                       .writerId(reviewEntity.getUserEntity().getId())
                       .writerProfileImagePath(reviewEntity.getUserEntity().getCustomerProfileFileEntity().getFilePath())
                       .createdDate(String.valueOf(reviewEntity.getCreatedDate()))
                       .build();
               reviewResponseDtoList.add(reviewResponseDto);
           }
        }

        // 리뷰 최신순으로 정렬
        Comparator<ReviewResponseDto> createdAtComparator = Comparator.comparing(ReviewResponseDto::getCreatedDate).reversed();
        Collections.sort(reviewResponseDtoList, createdAtComparator);


        return reviewResponseDtoList;


    }
}
