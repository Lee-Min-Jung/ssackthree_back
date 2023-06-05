package com.ssackthree.ssackthree_back.service;

import com.google.maps.model.LatLng;
import com.ssackthree.ssackthree_back.dto.TownRegisterProductRequestDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.TownProductStatusEnum;
import com.ssackthree.ssackthree_back.repository.*;
import com.ssackthree.ssackthree_back.util.FileService;
import com.ssackthree.ssackthree_back.util.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MyTownService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final LocationService locationService;
    private final MyTownProductFileRepository myTownProductFileRepository;
    private final MyTownProductStatusRepository myTownProductStatusRepository;
    private final MyTownProductHopingPlaceRepository myTownProductHopingPlaceRepository;
    private final MyTownProductRepository myTownProductRepository;

    // 상품 저장
    public int registerProduct(TownRegisterProductRequestDto townRegisterProductRequestDto, MultipartFile[] products) throws Exception {
        log.info("registerProduct 서비스 시작");
        // 상품 저장
        MyTownProductEntity myTownProductEntity = MyTownProductEntity.builder()
                .title(townRegisterProductRequestDto.getTitle())
                .price(townRegisterProductRequestDto.getPrice())
                .content(townRegisterProductRequestDto.getContent())
                .hopingPlaceAddress(townRegisterProductRequestDto.getHopingPlaceAddress())
                .userEntity(userRepository.findById(townRegisterProductRequestDto.getUserId()).get())
                .createdDate(LocalDateTime.now())
                .build();

        myTownProductRepository.save(myTownProductEntity);

        log.info("registerProduct 상품 저장 완료");


        // 파일 저장
        registerProductImageFile(products, myTownProductEntity);

        log.info("registerProduct 파일 저장 완료");


        // 상태 저장
        registerProductStatus(myTownProductEntity);

        log.info("registerProduct 상태 저장 완료");


        // 장소 저장
        registerProductHopingPlace(myTownProductEntity);
        log.info("registerProduct 장소 저장 완료");



        return 1;

    }

    // 상품 이미지 파일 저장
    public void registerProductImageFile(MultipartFile[] products, MyTownProductEntity myTownProductEntity) throws IOException {
        if(products != null){
            ArrayList<MyTownProductFileEntity> productFileEntities = new ArrayList<>();

            for(MultipartFile product : products){
                String savedMenuFileName = fileService.getSavedFileName(product);

                // s3에 파일 업로드
                fileService.uploadFile(product, savedMenuFileName);

                MyTownProductFileEntity productFileEntity = MyTownProductFileEntity.builder()
                        .fileOriginName(product.getOriginalFilename())
                        .fileName(savedMenuFileName)
                        .filePath(fileService.getUrl(savedMenuFileName))
                        .myTownProductEntity(myTownProductEntity)
                        .build();
                productFileEntities.add(productFileEntity);

            }
            myTownProductFileRepository.saveAll(productFileEntities);

        }
    }

    // 상품 상태 저장
    public void registerProductStatus(MyTownProductEntity myTownProductEntity){
        MyTownProductStatusEntity myTownProductStatusEntity = MyTownProductStatusEntity.builder()
                .productStatus(TownProductStatusEnum.SALE_ING)
                .myTownProductEntity(myTownProductEntity)
                .build();
        myTownProductStatusRepository.save(myTownProductStatusEntity);


    }

    // 상품 희망 거래 장소 저장
    public void registerProductHopingPlace(MyTownProductEntity myTownProductEntity) throws Exception{
        LatLng hopingPlace = locationService.getLocation(myTownProductEntity.getHopingPlaceAddress());

        MyTownProductHopingPlaceEntity myTownProductHopingPlaceEntity = MyTownProductHopingPlaceEntity.builder()
                .myTownProductEntity(myTownProductEntity)
                .latitude(hopingPlace.lat)
                .longitude(hopingPlace.lng)
                .build();

        myTownProductHopingPlaceRepository.save(myTownProductHopingPlaceEntity);
    }

}
