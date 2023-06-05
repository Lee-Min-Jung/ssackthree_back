package com.ssackthree.ssackthree_back.service;

import com.google.maps.model.LatLng;
import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.TownProductStatusEnum;
import com.ssackthree.ssackthree_back.repository.*;
import com.ssackthree.ssackthree_back.service.customizedClass.MenuIdDistance;
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
import java.util.*;

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
    private final UserLocationRepository userLocationRepository;

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

    // 상품 리스트
    public List<TownProductResponseDto> getTownProductList(TownHomeRequestDto townHomeRequestDto){
        // 해당 유저가 설정한 위치 정보
        Optional<UserLocationEntity> userLocation = userLocationRepository.findTopByUserEntityIdOrderByCreatedDateDesc(townHomeRequestDto.getUserId());

        // 거리 안에 있는 id
        List<Long> idList = getProductIdList(userLocation.get());

        // 조건에 맞는 상품들
        List<MyTownProductEntity> myTownProductEntity = myTownProductRepository.findAllById(idList);

        // 반환할 dto 생성
        List<TownProductResponseDto> productResponseDtoList = new ArrayList<>();
        for(MyTownProductEntity product : myTownProductEntity){
            TownProductResponseDto townProductResponseDto = TownProductResponseDto.builder()
                    .title(product.getTitle())
                    .hopingPlaceAddress(product.getHopingPlaceAddress())
                    .createdDate(product.getCreatedDate().toString())
                    .price(product.getPrice())
                    .imagePath(product.getMyTownProductFileEntityList().size() == 0 ? "" : product.getMyTownProductFileEntityList().get(0).getFilePath())
                    .build();
            productResponseDtoList.add(townProductResponseDto);
        }

        // 정렬
        productResponseDtoList = sort(townHomeRequestDto, productResponseDtoList);

        return productResponseDtoList;


    }

    // 특정 위치 안에 있는 상품 아이디 찾기
    public List<Long> getProductIdList(UserLocationEntity userLocation){
        List<MyTownProductHopingPlaceEntity> myTownProductHopingPlaceEntityList = myTownProductHopingPlaceRepository.findAll();
        List<Long> idList = new ArrayList<>();

        for(MyTownProductHopingPlaceEntity product : myTownProductHopingPlaceEntityList){
            double distance = locationService.getDistance(userLocation.getLatitude(), userLocation.getLongitude(), product.getLatitude(), product.getLongitude());
            if(distance <= userLocation.getM()){
                idList.add(product.getMyTownProductEntity().getId());
            }
        }

        return idList;
    }

    // 정렬
    public List<TownProductResponseDto> sort(TownHomeRequestDto townHomeRequestDto, List<TownProductResponseDto> productResponseDtoList){
        Comparator<TownProductResponseDto> createdAtComparator = Comparator.comparing(TownProductResponseDto::getCreatedDate).reversed();

        switch (townHomeRequestDto.getSortType()){
            case "latest":
                Collections.sort(productResponseDtoList, createdAtComparator);
                return productResponseDtoList;
            default:
                return productResponseDtoList;
        }

    }

}
