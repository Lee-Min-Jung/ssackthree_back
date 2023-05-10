package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
import com.ssackthree.ssackthree_back.repository.*;
import com.ssackthree.ssackthree_back.service.customizedClass.MenuIdDistance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final StoreLocationRepository storeLocationRepository;
    private final MenuLocationRepository menuLocationRepository;
    private final MenuFileRepository menuFileRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final MenuBargainningRepository menuBargainningRepository;
    private final UserLikeRepository userLikeRepository;

    public static final double EARTH_RADIUS = 6371.0088; // 지구 반지름 상수 선언

    @Value("${upload-path}")
    private String uploadPath;

    public void registerMenu(MenuRegisterRequestDto menuRegisterRequestDto, MultipartFile[] menus){

        MenuTypeEnum menuTypeEnum = getMenuType(menuRegisterRequestDto.getType());

        // 메뉴 내용
        Optional<StoreEntity> storeEntity = storeRepository.findByUserEntityId(menuRegisterRequestDto.getUserId());
        MenuEntity menuEntity = MenuEntity.builder()
                .name(menuRegisterRequestDto.getMenuName())
                .originalPrice(menuRegisterRequestDto.getOriginalPrice())
                .discountedPrice(menuRegisterRequestDto.getDiscountedPrice())
                .isBargainning(menuRegisterRequestDto.getIsBargainning())
                .type(menuTypeEnum)
                .endTime(menuRegisterRequestDto.getEndTime())
                .storeEntity(storeEntity.get())
                .createdDate(LocalDateTime.now())
                .build();
        menuRepository.save(menuEntity);

        // 메뉴 장소
        registerMenuLocation(storeEntity.get().getStoreLocationEntity(), menuEntity);

        // 메뉴 이미지
        registerMenuImageFile(menus, menuEntity);

        // 메뉴 상태
        registerMenuStatus(menuRegisterRequestDto.getIsBargainning(), menuEntity);

        // 흥정일 경우 흥정 세부 내용
        if(menuRegisterRequestDto.getIsBargainning().equals("T")){
            registerMenuBargainning(menuRegisterRequestDto.getMenuBargainningDto(), menuEntity);
        }
    }

    public void registerMenuBargainning(MenuBargainningDto menuBargainningDto, MenuEntity menuEntity){
        MenuBargainningEntity menuBargainningEntity = MenuBargainningEntity.builder()
                .limitTime(menuBargainningDto.getLimitTime())
                .minPrice(menuBargainningDto.getMinPrice())
                .menuEntity(menuEntity)
                .build();

        menuBargainningRepository.save(menuBargainningEntity);
    }

    public void registerMenuStatus(String isBargainning, MenuEntity menuEntity){
        MenuStatusEnum menuStatusEnum = MenuStatusEnum.ORDER_ING;
        if(isBargainning.equals("T")){
            menuStatusEnum = MenuStatusEnum.BARGAIN_ING;
        }
        MenuStatusEntity menuStatusEntity = MenuStatusEntity.builder()
                .menuStatus(menuStatusEnum)
                .menuEntity(menuEntity)
                .build();

        menuStatusRepository.save(menuStatusEntity);
    }

    public void registerMenuImageFile(MultipartFile[] menus, MenuEntity menuEntity){
        if(menus != null){
            ArrayList<MenuFileEntity> menuFileEntities = new ArrayList<>();

            for(MultipartFile menu : menus){
                String menuOriginName = menu.getOriginalFilename();
                UUID menuUuid = UUID.randomUUID();
                String menuSavedFileName = menuUuid.toString() + "_" + menuOriginName;
                String menuFilePath = uploadPath+menuSavedFileName;

                MenuFileEntity menuFileEntity = MenuFileEntity.builder()
                        .fileOriginName(menuOriginName)
                        .fileName(menuSavedFileName)
                        .filePath(menuFilePath)
                        .menuEntity(menuEntity)
                        .build();
                menuFileEntities.add(menuFileEntity);
                try {
                    menu.transferTo(new File(menuFilePath));
                } catch (IOException e) {
                    log.error("메뉴 사진 등록 실패");
                }
            }
            menuFileRepository.saveAll(menuFileEntities);

        }
    }

    public void registerMenuLocation(StoreLocationEntity storeLocationEntity, MenuEntity menuEntity){
        MenuLocationEntity menuLocationEntity = MenuLocationEntity.builder()
                .latitude(storeLocationEntity.getLatitude())
                .longitude(storeLocationEntity.getLongitude())
                .menuEntity(menuEntity)
                .build();
        menuLocationRepository.save(menuLocationEntity);
    }

    public MenuTypeEnum getMenuType(String menuTypeStr){
        switch (menuTypeStr){
            case "EXPIRATION":
                return MenuTypeEnum.EXPIRATION;
            case "WRONG_ORDER":
                return MenuTypeEnum.WRONG_ORDER;
            case "B":
                return MenuTypeEnum.B;
            default:
                return MenuTypeEnum.EXPIRATION;
        }
    }

//    public MenuStatusEnum getMenuStatus(String menuStatusStr){
//        switch (menuStatusStr){
//            case "orderIng":
//                return MenuStatusEnum.ORDER_ING;
//            case "orderCompleted":
//                return MenuStatusEnum.ORDER_COMPLETED;
//            case "bargainIng":
//                return MenuStatusEnum.BARGAIN_ING;
//            case "bargainCompleted":
//                return MenuStatusEnum.BARGAIN_COMPLETED;
//            case "bargainSuccess":
//                return MenuStatusEnum.BARGAIN_SUCCESS;
//            case "bargainFail":
//                return MenuStatusEnum.BARGAIN_FAIL;
//            default:
//                return MenuStatusEnum.ORDER_ING;
//        }
//    }


    public List<MenuInDistanceResponseDto> getMenuListInDistance(HomePageRequestDto homePageRequestDto){

        // 흥정 조건과 메뉴 타입 조건에 맞는 id 리스트
        List<Long> menuIdListForBarginAndType = menuRepository.findIdsByIsBargainningAndTypeIn(homePageRequestDto.getIsBargainning(), homePageRequestDto.getTypeList());

        // 위의 조건 만족하면서 거리 안에 있는 id와 거리 리스트
        List<MenuIdDistance> idDistanceList = getMenuIdDistance(homePageRequestDto, menuIdListForBarginAndType);
        List<Long> menuIdList = new ArrayList<>();
        List<Double> menuDistanceList = new ArrayList<>();

        for(MenuIdDistance menuIdDistance : idDistanceList){
            menuIdList.add(menuIdDistance.getId());
            menuDistanceList.add(menuIdDistance.getDistance());
        }

        // 모든 조건에 맞는 메뉴 엔티티
        List<MenuEntity> menuEntityList = menuRepository.findAllById(menuIdList);

        // 반환할 dto 생성
        int i = 0;
        List<MenuInDistanceResponseDto> menuInDistanceResponseDtoList = new ArrayList<>();
        for(MenuEntity menuEntity : menuEntityList){
            MenuInDistanceResponseDto menuInDistanceResponseDto = MenuInDistanceResponseDto.builder()
                    .menuId(menuEntity.getId())
                    .name(menuEntity.getName())
                    .originalPrice(menuEntity.getOriginalPrice())
                    .discountedPrice(menuEntity.getDiscountedPrice())
                    .storeName(menuEntity.getStoreEntity().getStoreName())
                    .distance(menuDistanceList.get(i))
                    .menuImagePath(menuEntity.getMenuFileEntity().get(0).getFilePath())
                    .likeCount(menuEntity.getUserLikeEntityList().size())
                    .createdDate(menuEntity.getCreatedDate())
                    .build();
            menuInDistanceResponseDtoList.add(menuInDistanceResponseDto);

            i++;
        }

        // 정렬
        menuInDistanceResponseDtoList = sort(homePageRequestDto, menuInDistanceResponseDtoList);

        return menuInDistanceResponseDtoList;




    }
    public MenuDetailDto setMenuDetailDto(MenuEntity menu){
        return MenuDetailDto.builder()
                .name(menu.getName())
                .originalPrice(menu.getOriginalPrice())
                .discountedPrice(menu.getDiscountedPrice())
                .isBargainning(menu.getIsBargainning())
                .bargainLimitTime(Optional.ofNullable(menu.getMenuBargainningEntity()).map(MenuBargainningEntity::getLimitTime).orElse(0))
                .saleEndTime(menu.getEndTime())
                .imagePath(Optional.ofNullable(menu.getMenuFileEntity().get(0)).map(MenuFileEntity::getFilePath).orElse(""))
                .build();
    }

    public MenuOtherDto setMenuOtherDto(MenuEntity menu){
        return MenuOtherDto.builder()
                .name(menu.getName())
                .originamlPrice(menu.getOriginalPrice())
                .discountedPrice(menu.getDiscountedPrice())
                .imagePath(Optional.ofNullable(menu.getMenuFileEntity().get(0)).map(MenuFileEntity::getFilePath).orElse(""))
                .build();
    }

    public MenuStoreDto setMenuStoreDto(StoreEntity store){
        return MenuStoreDto.builder()
                .storeName(store.getStoreName())
                .startTime(store.getStartTime())
                .endTime(store.getEndTime())
                .holiday(store.getHoliday())
                .phoneNumber(store.getPhoneNumber())
                .longitude(store.getStoreLocationEntity().getLongitude())
                .latitude(store.getStoreLocationEntity().getLatitude())
                .mainAddress(store.getMainAddress())
                .detailAddress(store.getDetailAddress())
                .storeImagePath(Optional.ofNullable(store.getStoreProfileFileEntity()).map(StoreProfileFileEntity::getFilePath).orElse(""))
                .build();
    }

    public MenuDetailResponseDto getMenuDetail(long menuId){
        Optional<MenuEntity> menu = menuRepository.findById(menuId);

        if(menu.isPresent()){
            // 메뉴 디테일 정보
            MenuDetailDto menuDetailDto = setMenuDetailDto(menu.get());

            // 다른 메뉴 정보
            List<MenuOtherDto> menuOtherDtoList = new ArrayList<>();
            for(MenuEntity m : menu.get().getStoreEntity().getMenuEntityList()){
                if(!menu.get().equals(m)){
                    menuOtherDtoList.add(setMenuOtherDto(m));
                }
            }

            // 가게 정보
            MenuStoreDto menuStoreDto = setMenuStoreDto(menu.get().getStoreEntity());

            // 응답 dto 설정
            MenuDetailResponseDto menuDetailResponseDto = MenuDetailResponseDto.builder()
                    .menuDetail(menuDetailDto)
                    .menuOther(menuOtherDtoList)
                    .menuStore(menuStoreDto)
                    .build();

            return menuDetailResponseDto;
        }

        return null;
    }


    public List<MenuInDistanceResponseDto> sort(HomePageRequestDto homePageRequestDto, List<MenuInDistanceResponseDto> menuInDistanceResponseDto){
        Comparator<MenuInDistanceResponseDto> createdAtComparator = Comparator.comparing(MenuInDistanceResponseDto::getCreatedDate);
        Comparator<MenuInDistanceResponseDto> distanceComparator = Comparator.comparing(MenuInDistanceResponseDto::getDistance);
        Comparator<MenuInDistanceResponseDto> likeComparator = Comparator.comparing(MenuInDistanceResponseDto::getLikeCount).reversed();

        switch (homePageRequestDto.getSortType()){
            case "latest":
                Collections.sort(menuInDistanceResponseDto, createdAtComparator);
                return menuInDistanceResponseDto;
            case "shortest":
                Collections.sort(menuInDistanceResponseDto, distanceComparator);
                return menuInDistanceResponseDto;
            case "like":
                Collections.sort(menuInDistanceResponseDto, likeComparator);
                return menuInDistanceResponseDto;
            default:
                return menuInDistanceResponseDto;
        }

    }

    public List<MenuIdDistance> getMenuIdDistance(HomePageRequestDto homePageRequestDto, List<Long> idList){
        List<MenuLocationEntity> menuLocationEntityList = menuLocationRepository.findAll();
        List<MenuIdDistance> menuIdDistanceList = new ArrayList<>();
        for(MenuLocationEntity menuLocation : menuLocationEntityList){
            if(idList.contains(menuLocation.getMenuEntity().getId())){
                double distance = getDistance(homePageRequestDto.getLatitude(), homePageRequestDto.getLongitude(), menuLocation.getLatitude(), menuLocation.getLongitude());
                if(distance <= homePageRequestDto.getKm()){
                    menuIdDistanceList.add(new MenuIdDistance(menuLocation.getMenuEntity().getId(), distance));
                }
            }
        }

        return menuIdDistanceList;
    }


    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d =EARTH_RADIUS* c * 1000;
        return d;
    }
}


