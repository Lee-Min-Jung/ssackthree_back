package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuLocationRepository menuLocationRepository;
    private final MenuFileRepository menuFileRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final MenuBargainningRepository menuBargainningRepository;
    private final FileService fileService;
    private final UserLocationRepository userLocationRepository;
    private final LocationService locationService;

    public static final double EARTH_RADIUS = 6371.0088; // 지구 반지름 상수 선언

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 점주 메뉴 등록
    public void registerMenu(MenuRegisterRequestDto menuRegisterRequestDto) throws IOException {

        MenuTypeEnum menuTypeEnum = getMenuType(menuRegisterRequestDto.getType());

        // 메뉴 내용
        Optional<StoreEntity> storeEntity = storeRepository.findByUserEntityId(menuRegisterRequestDto.getUserId());
        MenuEntity menuEntity = MenuEntity.builder()
                .name(menuRegisterRequestDto.getMenuName())
                .originalPrice(menuRegisterRequestDto.getOriginalPrice())
                .discountedPrice(menuRegisterRequestDto.getDiscountedPrice())
                .isBargainning(menuRegisterRequestDto.getIsBargainning())
                .type(menuTypeEnum)
                .endTime(LocalDateTime.parse(menuRegisterRequestDto.getEndTime(), formatter))
                .storeEntity(storeEntity.get())
                .createdDate(LocalDateTime.now())
                .build();
        menuRepository.save(menuEntity);

        // 메뉴 장소
        registerMenuLocation(storeEntity.get().getStoreLocationEntity(), menuEntity);

        // 메뉴 이미지
        registerMenuImageFile(menuRegisterRequestDto.getMenuImages(), menuEntity);

        // 메뉴 상태
        registerMenuStatus(menuRegisterRequestDto.getIsBargainning(), menuEntity);

        // 흥정일 경우 흥정 세부 내용
        if(menuRegisterRequestDto.getIsBargainning().equals("T")){
            registerMenuBargainning(menuRegisterRequestDto.getLimitTime(), menuRegisterRequestDto.getMinPrice(), menuEntity);
        }
    }

    // 메뉴 등록 중 흥정 내용 저장
    public void registerMenuBargainning(int limitTime, int minPrice, MenuEntity menuEntity){
        MenuBargainningEntity menuBargainningEntity = MenuBargainningEntity.builder()
                .limitTime(limitTime)
                .minPrice(minPrice)
                .bargainEnd(getAfterTime(limitTime))
                .menuEntity(menuEntity)
                .build();

        menuBargainningRepository.save(menuBargainningEntity);
    }

    // 메뉴 등록 중 메뉴 상태 저장
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

    // 메뉴 등록 중 메뉴 이미지 저장
    public void registerMenuImageFile(List<MultipartFile> menus, MenuEntity menuEntity) throws IOException {
        if(menus != null){
            ArrayList<MenuFileEntity> menuFileEntities = new ArrayList<>();

            for(MultipartFile menu : menus){
                String savedMenuFileName = fileService.getSavedFileName(menu);

                // s3에 파일 업로드
                fileService.uploadFile(menu, savedMenuFileName);

                MenuFileEntity menuFileEntity = MenuFileEntity.builder()
                        .fileOriginName(menu.getOriginalFilename())
                        .fileName(savedMenuFileName)
                        .filePath(fileService.getUrl(savedMenuFileName))
                        .menuEntity(menuEntity)
                        .build();
                menuFileEntities.add(menuFileEntity);

            }
            menuFileRepository.saveAll(menuFileEntities);

        }
    }

    // 메뉴 등록 중 메뉴 위치 저장
    public void registerMenuLocation(StoreLocationEntity storeLocationEntity, MenuEntity menuEntity){
        MenuLocationEntity menuLocationEntity = MenuLocationEntity.builder()
                .latitude(storeLocationEntity.getLatitude())
                .longitude(storeLocationEntity.getLongitude())
                .menuEntity(menuEntity)
                .build();
        menuLocationRepository.save(menuLocationEntity);
    }

    // 특정 시간 지난 결과 구하기
    public LocalDateTime getAfterTime(int period){
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime afterTime = currentTime.plusMinutes(period);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedAfterTime = afterTime.format(formatter);
        return afterTime;
    }

    // 메뉴 등록 중 메뉴 type 구하기
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

    //    @Scheduled(fixedRate = 60000) // 60초마다 실행
//    public void updateBargainningEndTime(){
//        log.info("*****************************************");
//        List<MenuBargainningEntity> menuBargainningEntityList = menuBargainningRepository.findAll();
//
//        for(MenuBargainningEntity menuBargainning : menuBargainningEntityList){
//            if(menuBargainning.getBargainEnd().isBefore(LocalDateTime.now())){ // 마감 시간 지남
//                if(menuBargainning.getMenuEntity().getBargainOrderEntityList().size() == 0){ // 흥정 주문 0개
//                    LocalDateTime updatedBargainEnd = getAfterTime(menuBargainning.getLimitTime());
//                    MenuBargainningEntity menuBargainningEntity = MenuBargainningEntity.builder()
//                            .id(menuBargainning.getId())
//                            .limitTime(menuBargainning.getLimitTime())
//                            .minPrice(menuBargainning.getMinPrice())
//                            .menuEntity(menuBargainning.getMenuEntity())
//                            .bargainEnd(updatedBargainEnd)
//                            .build();
//                    menuBargainningRepository.save(menuBargainningEntity);
//                }
//            }
//        }
//    }

    // 점주 홈페이지 화면 메뉴 리스트
    public List<StoreMenuListResponseDto> getStoreMenuList(StoreMenuListRequestDto storeMenuListRequestDto){
        List<StoreMenuListResponseDto> storeMenuListResponseDtoList = new ArrayList<>();

        // userId별 메뉴 찾기
        long storeId = storeRepository.findByUserEntityId(storeMenuListRequestDto.getUserId()).get().getId();
        Optional<List<MenuEntity>> menuEntityList = menuRepository.findByStoreEntityIdAndIsBargainningAndMenuStatus(storeId, storeMenuListRequestDto.getIsBargain());

        // 메뉴 리스트 생성
        if(menuEntityList.isPresent()){
            for(MenuEntity menu : menuEntityList.get()){
                StoreMenuListResponseDto storeMenuListResponseDto = StoreMenuListResponseDto.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .originalPrice(menu.getOriginalPrice())
                        .discountedPrice(menu.getDiscountedPrice())
                        .imageUrl(menu.getMenuFileEntity().get(0).getFilePath())
                        .proposerCount(menu.getBargainOrderEntityList().size())
                        .build();
                storeMenuListResponseDtoList.add(storeMenuListResponseDto);
            }
        }

        return storeMenuListResponseDtoList;



    }

    // 손님 홈페이지 화면 메뉴 리스트
    public List<MenuInDistanceResponseDto> getMenuListInDistance(HomePageRequestDto homePageRequestDto){

        // 흥정 조건과 메뉴 타입 조건에 맞는 id 리스트
        List<Long> menuIdListForBarginAndType = menuRepository.findIdsByIsBargainningAndTypeIn(homePageRequestDto.getIsBargainning(), homePageRequestDto.getTypeList());
        // 해당 유저가 설정한 위치 정보
        Optional<UserLocationEntity> userLocation = userLocationRepository.findTopByUserEntityIdOrderByCreatedDateDesc(homePageRequestDto.getUserId());
        // 위의 조건 만족하면서 거리 안에 있는 id와 거리 리스트
        List<MenuIdDistance> idDistanceList = new ArrayList<MenuIdDistance>();
        if(userLocation.isPresent()){ // 만약 user의 위치가 등록되어 있는 경우
            idDistanceList = getMenuIdDistance(userLocation.get(), menuIdListForBarginAndType);
        }
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
                    .likeCount(menuEntity.getUserMenuLikeEntityList().size())
                    .isLike(isMenuLike(menuEntity, homePageRequestDto.getUserId()))
                    .createdDate(menuEntity.getCreatedDate())
                    .build();
            menuInDistanceResponseDtoList.add(menuInDistanceResponseDto);

            i++;
        }

        // 정렬
        menuInDistanceResponseDtoList = sort(homePageRequestDto, menuInDistanceResponseDtoList);

        return menuInDistanceResponseDtoList;




    }

    // 특정 위치 안에 있는지 확인
    public List<MenuIdDistance> getMenuIdDistance(UserLocationEntity userLocation, List<Long> idList){
        List<MenuLocationEntity> menuLocationEntityList = menuLocationRepository.findAll();
        List<MenuIdDistance> menuIdDistanceList = new ArrayList<>();
        for(MenuLocationEntity menuLocation : menuLocationEntityList){
            if(idList.contains(menuLocation.getMenuEntity().getId())){
                double distance = locationService.getDistance(userLocation.getLatitude(), userLocation.getLongitude(), menuLocation.getLatitude(), menuLocation.getLongitude());
                if(distance <= userLocation.getM()){
                    menuIdDistanceList.add(new MenuIdDistance(menuLocation.getMenuEntity().getId(), distance));
                }
            }
        }
        return menuIdDistanceList;
    }

    // 사용자가 메뉴에 좋아요 눌렀는지 확인
    public String isMenuLike(MenuEntity menuEntity, long userId){
        for(UserMenuLikeEntity user : menuEntity.getUserMenuLikeEntityList()){
            if(user.getUserEntity().getId() == userId){
                return "T";
            }
        }
        return "F";
    }

    // 사용자가 식당에 좋아요 눌렀는지 확인
    public String isStoreLike(StoreEntity storeEntity, long userId){
        for(UserStoreLikeEntity user : storeEntity.getUserStoreLikeEntityList()){
            if(user.getUserEntity().getId() == userId){
                return "T";
            }
        }
        return "F";
    }

    public MenuDetailDto setMenuDetailDto(MenuEntity menu, long userId){
        return MenuDetailDto.builder()
                .name(menu.getName())
                .originalPrice(menu.getOriginalPrice())
                .discountedPrice(menu.getDiscountedPrice())
                .isBargainning(menu.getIsBargainning())
                .bargainLimitTime(Optional.ofNullable(menu.getMenuBargainningEntity()).map(MenuBargainningEntity::getLimitTime).orElse(0))
                .saleEndTime(menu.getEndTime())
                .minPrice(menu.getMenuBargainningEntity() != null ? menu.getMenuBargainningEntity().getMinPrice() : 0)
                .imagePath(Optional.ofNullable(menu.getMenuFileEntity().get(0)).map(MenuFileEntity::getFilePath).orElse(""))
                .isMenuLike(isMenuLike(menu, userId))
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

    public MenuStoreDto setMenuStoreDto(StoreEntity store, long userId){
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
                .isStoreLike(isStoreLike(store, userId))
                .storeImagePath(Optional.ofNullable(store.getStoreProfileFileEntity()).map(StoreProfileFileEntity::getFilePath).orElse(""))
                .build();
    }

    public MenuDetailResponseDto getMenuDetail(long menuId, long userId){
        Optional<MenuEntity> menu = menuRepository.findById(menuId);

        if(menu.isPresent()){
            // 메뉴 디테일 정보
            MenuDetailDto menuDetailDto = setMenuDetailDto(menu.get(), userId);

            // 다른 메뉴 정보
            List<MenuOtherDto> menuOtherDtoList = new ArrayList<>();
            for(MenuEntity m : menu.get().getStoreEntity().getMenuEntityList()){
                if(!menu.get().equals(m) && (m.getMenuStatusEntity().getMenuStatus() == MenuStatusEnum.ORDER_ING || m.getMenuStatusEntity().getMenuStatus() == MenuStatusEnum.BARGAIN_ING)){
                    menuOtherDtoList.add(setMenuOtherDto(m));
                }
            }

            // 가게 정보
            MenuStoreDto menuStoreDto = setMenuStoreDto(menu.get().getStoreEntity(), userId);

            // 응답 dto 설정
            MenuDetailResponseDto menuDetailResponseDto = MenuDetailResponseDto.builder()
                    .writerId(menu.get().getStoreEntity().getUserEntity().getId())
                    .menuDetail(menuDetailDto)
                    .menuOther(menuOtherDtoList)
                    .menuStore(menuStoreDto)
                    .build();

            return menuDetailResponseDto;
        }

        return null;
    }

    // 점주 마이페이지 이전 판매 내역
    public List<MyPageSellListResponseDto> getMyPageSellCompleteList(long userId){
        Optional<StoreEntity> store = storeRepository.findByUserEntityId(userId);
        List<MyPageSellListResponseDto> myPageBeforeSellListResponseDtoList = new ArrayList<>();

        if(store.isPresent()){
            for(MenuEntity m : store.get().getMenuEntityList()){
                if(m.getMenuStatusEntity().getMenuStatus().toString().equals("ORDER_COMPLETED") || m.getMenuStatusEntity().getMenuStatus().toString().equals("BARGAIN_COMPLETED")){
                    MyPageSellListResponseDto myPageSellListResponseDto = MyPageSellListResponseDto.builder()
                            .menuName(m.getName())
                            .originalPrice(m.getOriginalPrice())
                            .discountedPrice(m.getDiscountedPrice())
                            .imagePath(m.getMenuFileEntity().get(0).getFilePath())
                            .isBargain(m.getIsBargainning())
                            .date(String.valueOf(m.getOrderEntity().getCreatedDate()))
                            .buyer(m.getOrderEntity().getUserEntity().getRepName())
                            .build();
                    myPageBeforeSellListResponseDtoList.add(myPageSellListResponseDto);
                }
            }
        }

        return myPageBeforeSellListResponseDtoList;
    }


    public List<MenuInDistanceResponseDto> sort(HomePageRequestDto homePageRequestDto, List<MenuInDistanceResponseDto> menuInDistanceResponseDto){
        Comparator<MenuInDistanceResponseDto> createdAtComparator = Comparator.comparing(MenuInDistanceResponseDto::getCreatedDate).reversed();
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


}