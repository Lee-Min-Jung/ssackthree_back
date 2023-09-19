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
    public int registerProduct(TownRegisterProductRequestDto townRegisterProductRequestDto) throws Exception {
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
        registerProductImageFile(townRegisterProductRequestDto.getProductImages(), myTownProductEntity);

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
    public void registerProductImageFile(List<MultipartFile> products, MyTownProductEntity myTownProductEntity) throws IOException {
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

        // 본인 상품 보기 선택한 경우
        if(townHomeRequestDto.getIsMine().equals("T")){
            List<MyTownProductEntity> myTownProductEntityList = myTownProductRepository.findByUserEntityId(townHomeRequestDto.getUserId()).get();
            List<TownProductResponseDto> townProductResponseDtoList = new ArrayList<>();

            for(MyTownProductEntity product : myTownProductEntityList){
                TownProductResponseDto townProductResponseDto = TownProductResponseDto.builder()
                        .productId(product.getId())
                        .title(product.getTitle())
                        .hopingPlaceAddress(product.getHopingPlaceAddress())
                        .createdDate(product.getCreatedDate().toString())
                        .price(product.getPrice())
                        .imagePath(product.getMyTownProductFileEntityList().size() == 0 ? "" : product.getMyTownProductFileEntityList().get(0).getFilePath())
                        .build();
                townProductResponseDtoList.add(townProductResponseDto);
            }
            return townProductResponseDtoList;
        }

        // 해당 유저가 설정한 위치 정보
        Optional<UserLocationEntity> userLocation = userLocationRepository.findTopByUserEntityIdOrderByCreatedDateDesc(townHomeRequestDto.getUserId());

        // 거리 안에 있는 id와 거리
        List<MenuIdDistance> idDistanceList = getProductIdList(userLocation.get(), townHomeRequestDto.getUserId());

        // id와 거리 분리
        List<Long> productIdList = new ArrayList<>();
        List<Double> productDistanceList = new ArrayList<>();
        for(MenuIdDistance menuIdDistance : idDistanceList){
            productIdList.add(menuIdDistance.getId());
            productDistanceList.add(menuIdDistance.getDistance());
        }


        // 조건에 맞는 상품들
        List<MyTownProductEntity> myTownProductEntity = myTownProductRepository.findAllById(productIdList);

        // 반환할 dto 생성
        int i = 0;
        List<TownProductResponseDto> productResponseDtoList = new ArrayList<>();
        for(MyTownProductEntity product : myTownProductEntity){
            TownProductResponseDto townProductResponseDto = TownProductResponseDto.builder()
                    .productId(product.getId())
                    .title(product.getTitle())
                    .hopingPlaceAddress(product.getHopingPlaceAddress())
                    .createdDate(product.getCreatedDate().toString())
                    .price(product.getPrice())
                    .distance(productDistanceList.get(i))
                    .isLike(isTownLike(product, townHomeRequestDto.getUserId()))
                    .imagePath(product.getMyTownProductFileEntityList().size() == 0 ? "" : product.getMyTownProductFileEntityList().get(0).getFilePath())
                    .build();

            productResponseDtoList.add(townProductResponseDto);
            i++;
        }

        // 정렬
        productResponseDtoList = sort(townHomeRequestDto, productResponseDtoList);

        return productResponseDtoList;


    }

    // 사용자가 우리동네 상품에 좋아요 눌렀는지 확인
    public String isTownLike(MyTownProductEntity townEntity, long userId){
        for(UserTownLikeEntity user : townEntity.getUserTownLikeEntityList()){
            if(user.getUserEntity().getId() == userId){
                return "T";
            }
        }
        return "F";
    }

    // 특정 위치 안에 있는 상품 아이디 찾기
    public List<MenuIdDistance> getProductIdList(UserLocationEntity userLocation, long userId){
        List<MyTownProductHopingPlaceEntity> myTownProductHopingPlaceEntityList = myTownProductHopingPlaceRepository.findAll();
        List<MenuIdDistance> idDistanceList = new ArrayList<>();

        for(MyTownProductHopingPlaceEntity product : myTownProductHopingPlaceEntityList){
            double distance = locationService.getDistance(userLocation.getLatitude(), userLocation.getLongitude(), product.getLatitude(), product.getLongitude());
            if(distance <= userLocation.getM() && product.getMyTownProductEntity().getUserEntity().getId() != userId){
                idDistanceList.add(new MenuIdDistance(product.getMyTownProductEntity().getId(), distance));
            }
        }

        return idDistanceList;
    }

    // 정렬
    public List<TownProductResponseDto> sort(TownHomeRequestDto townHomeRequestDto, List<TownProductResponseDto> productResponseDtoList){
        Comparator<TownProductResponseDto> createdAtComparator = Comparator.comparing(TownProductResponseDto::getCreatedDate).reversed();
        Comparator<TownProductResponseDto> distanceComparator = Comparator.comparing(TownProductResponseDto::getDistance);

        switch (townHomeRequestDto.getSortType()){
            case "latest":
                Collections.sort(productResponseDtoList, createdAtComparator);
                return productResponseDtoList;
            case "shortest":
                Collections.sort(productResponseDtoList, distanceComparator);
                return productResponseDtoList;
            default:
                return productResponseDtoList;
        }

    }

    // 상품 디테일
    public TownProductDetailResponseDto getTownProductDetail(long productId, long userId){
        MyTownProductEntity myTownProductEntity = myTownProductRepository.findById(productId).get();

        // 해당 상품의 판매자가 올린 다른 상품 리스트 구하기
        List<TownOtherProductResponseDto> townOtherProductResponseDtoList = getTownOtherProductList(myTownProductEntity);

        TownProductDetailResponseDto townProductDetailResponseDto = TownProductDetailResponseDto.builder()
                .writerId(myTownProductEntity.getUserEntity().getId())
                .writerNickname(myTownProductEntity.getUserEntity().getRepName())
                .productId(myTownProductEntity.getId())
                .title(myTownProductEntity.getTitle())
                .content(myTownProductEntity.getContent())
                .createdDate(myTownProductEntity.getCreatedDate().toString())
                .hopingPlaceAddress(myTownProductEntity.getHopingPlaceAddress())
                .latitude(myTownProductEntity.getMyTownProductHopingPlace().getLatitude())
                .longitude(myTownProductEntity.getMyTownProductHopingPlace().getLongitude())
                .price(myTownProductEntity.getPrice())
                .status(myTownProductEntity.getMyTownProductStatusEntity().getProductStatus().toString())
                .isLike(isTownLike(myTownProductEntity, userId))
                .imagePath(myTownProductEntity.getMyTownProductFileEntityList().size() == 0 ? "" : myTownProductEntity.getMyTownProductFileEntityList().get(0).getFilePath())
                .townOtherProductResponseDtoList(townOtherProductResponseDtoList)
                .build();

        return townProductDetailResponseDto;
    }

    // 판매자의 다른 상품
    public List<TownOtherProductResponseDto> getTownOtherProductList(MyTownProductEntity myTownProductEntity){
        List<TownOtherProductResponseDto> townOtherProductResponseDtoList = new ArrayList<>();

        for(MyTownProductEntity product : myTownProductEntity.getUserEntity().getMyTownProductEntityList()){
            if(!product.equals(myTownProductEntity) && product.getMyTownProductStatusEntity().getProductStatus() == TownProductStatusEnum.SALE_ING){
                TownOtherProductResponseDto townOtherProductResponseDto = TownOtherProductResponseDto.builder()
                        .title(product.getTitle())
                        .price(product.getPrice())
                        .build();
                townOtherProductResponseDtoList.add(townOtherProductResponseDto);


            }
        }

        return townOtherProductResponseDtoList;

    }

    // 마이페이지 우리동네 판매 내역
    public List<TownProductResponseDto> getTownSellList(TownSellListRequestDto townSellListRequestDto){
        Optional<UserEntity> user = userRepository.findById(townSellListRequestDto.getUserId());
        List<TownProductResponseDto> townProductResponseDtoList = new ArrayList<>();

        if(user.isPresent()){
            for(MyTownProductEntity tp : user.get().getMyTownProductEntityList()){
                if(tp.getMyTownProductStatusEntity().getProductStatus().toString().equals(townSellListRequestDto.getType())){
                    TownProductResponseDto townProductResponseDto = TownProductResponseDto.builder()
                            .productId(tp.getId())
                            .price(tp.getPrice())
                            .title(tp.getTitle())
                            .createdDate(String.valueOf(tp.getCreatedDate()))
                            .hopingPlaceAddress(tp.getHopingPlaceAddress())
                            .imagePath(tp.getMyTownProductFileEntityList().get(0).getFilePath())
                            .build();
                    townProductResponseDtoList.add(townProductResponseDto);
                }
            }
        }

        return townProductResponseDtoList;
    }

    // 판매 완료로 게시글 수정
    public void updateTownProductStats(TownProductStatusUpdateRequestDto townProductStatusUpdateRequestDto){
        // 기존 상태 삭제
        myTownProductStatusRepository.deleteByMyTownProductEntityId(townProductStatusUpdateRequestDto.getTownProductId());

        // 수정한 상태 저장
        TownProductStatusEnum status = TownProductStatusEnum.SALE_COMPLETE;
        if(townProductStatusUpdateRequestDto.getStatus().equals("I")){
            status = TownProductStatusEnum.SALE_ING;
        }else if(townProductStatusUpdateRequestDto.getStatus().equals("C")){
            status = TownProductStatusEnum.SALE_COMPLETE;
        }

        MyTownProductStatusEntity myTownProductStatusEntity = MyTownProductStatusEntity.builder()
                .productStatus(status)
                .myTownProductEntity(myTownProductRepository.findById(townProductStatusUpdateRequestDto.getTownProductId()).get())
                .build();

        myTownProductStatusRepository.save(myTownProductStatusEntity);
    }

}
