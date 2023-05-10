package com.ssackthree.ssackthree_back.service;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.ssackthree.ssackthree_back.dto.StoreRegisterRequestDto;
import com.ssackthree.ssackthree_back.dto.StoreRegisterResponseDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreProfileFileRepository storeProfileFileRepository;
    private final StoreMenuFileRepository storeMenuFileRepository;
    private final StoreLocationRepository storeLocationRepository;

    @Value("${upload-path}")
    private String uploadPath;

    @Value("${google.api.key}")
    private String apiKey;

    public void registerStore(StoreRegisterRequestDto storeRegisterRequestDto, MultipartFile profile, MultipartFile[] menus) throws Exception{

        //가게 일반 내용 저장
        long userId = storeRegisterRequestDto.getUserId();
        Optional<UserEntity> user = userRepository.findById(userId);
        StoreEntity storeEntity = StoreEntity.builder()
                .storeName(storeRegisterRequestDto.getStoreName())
                .mainAddress(storeRegisterRequestDto.getMainAddress())
                .detailAddress(storeRegisterRequestDto.getDetailAddress())
                .zipcode(storeRegisterRequestDto.getZipcode())
                .phoneNumber(storeRegisterRequestDto.getPhoneNumber())
                .startTime(storeRegisterRequestDto.getStartTime())
                .endTime(storeRegisterRequestDto.getEndTime())
                .holiday(storeRegisterRequestDto.getHoliday())
                .introduce(storeRegisterRequestDto.getIntroduce())
                .userEntity(user.get())
                .build();
        storeRepository.save(storeEntity);
        registerLocation(storeEntity, storeRegisterRequestDto.getMainAddress());

        // 프로필 사진 저장
        if(profile != null){
            String profileOriginName = profile.getOriginalFilename();
            UUID profileUuid = UUID.randomUUID();
            String profileSavedFileName = profileUuid.toString() + "_" + profileOriginName;
            String profileFilePath = uploadPath+profileSavedFileName;
            StoreProfileFileEntity storeProfileFileEntity = StoreProfileFileEntity.builder()
                    .fileOriginName(profileOriginName)
                    .fileName(profileSavedFileName)
                    .filePath(profileFilePath)
                    .storeEntity(storeEntity)
                    .build();
            storeProfileFileRepository.save(storeProfileFileEntity);
            try {
                profile.transferTo(new File(profileFilePath));
            } catch (IOException e) {
                log.error("가게 프로필 사진 등록 실패");
            }
        }

        // 가게 메뉴 저장
        if(menus != null && menus.length != 0){
            ArrayList<StoreMenuFileEntity> storeMenuFileEntities = new ArrayList<>();

            for(MultipartFile menu : menus){
                String menuOriginName = menu.getOriginalFilename();
                UUID menuUuid = UUID.randomUUID();
                String menuSavedFileName = menuUuid.toString() + "_" + menuOriginName;
                String menuFilePath = uploadPath+menuSavedFileName;
                StoreMenuFileEntity storeMenuFileEntity = StoreMenuFileEntity.builder()
                        .fileOriginName(menuOriginName)
                        .fileName(menuSavedFileName)
                        .filePath(menuFilePath)
                        .storeEntity(storeEntity)
                        .build();
                storeMenuFileEntities.add(storeMenuFileEntity);
                try {
                    menu.transferTo(new File(menuFilePath));
                } catch (IOException e) {
                    log.error("메뉴 사진 등록 실패");
                }
            }
            storeMenuFileRepository.saveAll(storeMenuFileEntities);

        }



    }

    public LatLng getLocation(String address) throws Exception{
        if(address.equals("")){
            return null;
        }
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
        if(results.length != 0){
            LatLng location = results[0].geometry.location;
            return location;
        }
        return null;
    }

    public void registerLocation(StoreEntity storeEntity, String address) throws Exception{
        LatLng location = getLocation(address);
        if(location != null){
            double latitude = location.lat;
            double longitude = location.lng;
            StoreLocationEntity storeLocationEntity = StoreLocationEntity.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .storeEntity(storeEntity)
                    .build();
            storeLocationRepository.save(storeLocationEntity);
        }
    }

    public void updateLocation(StoreEntity storeEntity, String address) throws Exception{
        LatLng location = getLocation(address);
        if(location != null){
            Optional<StoreLocationEntity> storeLocationEntity = storeLocationRepository.findByStoreEntityId(storeEntity.getId());
            double latitude = location.lat;
            double longitude = location.lng;
            StoreLocationEntity savedStoreLocationEntity = StoreLocationEntity.builder()
                    .id(storeLocationEntity.get().getId())
                    .latitude(latitude)
                    .longitude(longitude)
                    .storeEntity(storeEntity)
                    .build();
            storeLocationRepository.save(savedStoreLocationEntity);
        }
    }
    public void updateStore(StoreRegisterRequestDto storeRegisterRequestDto, MultipartFile profile, MultipartFile[] menus) throws Exception{
        long userId = storeRegisterRequestDto.getUserId();
        Optional<UserEntity> user = userRepository.findById(userId);
        // 가게 내용
        StoreEntity storeEntity = StoreEntity.builder()
                .id(storeRegisterRequestDto.getId())
                .storeName(storeRegisterRequestDto.getStoreName())
                .mainAddress(storeRegisterRequestDto.getMainAddress())
                .detailAddress(storeRegisterRequestDto.getDetailAddress())
                .zipcode(storeRegisterRequestDto.getZipcode())
                .phoneNumber(storeRegisterRequestDto.getPhoneNumber())
                .startTime(storeRegisterRequestDto.getStartTime())
                .endTime(storeRegisterRequestDto.getEndTime())
                .holiday(storeRegisterRequestDto.getHoliday())
                .introduce(storeRegisterRequestDto.getIntroduce())
                .userEntity(user.get())
                .build();
        storeRepository.save(storeEntity);
        updateLocation(storeEntity, storeEntity.getMainAddress());

        //가게 프로필
        if(profile != null){
            String profileOriginName = profile.getOriginalFilename();
            UUID profileUuid = UUID.randomUUID();
            String profileSavedFileName = profileUuid.toString() + "_" + profileOriginName;
            String profileFilePath = uploadPath+profileSavedFileName;

            Optional<StoreProfileFileEntity> storeProfileFileEntity = storeProfileFileRepository.findByStoreEntityId(storeRegisterRequestDto.getId());

            if(storeProfileFileEntity.isPresent()){
                String deletePath = storeProfileFileEntity.get().getFilePath();
                deleteFile(deletePath);
                StoreProfileFileEntity savedStoreProfileFileEntity = StoreProfileFileEntity.builder()
                        .id(storeProfileFileEntity.get().getId())
                        .fileOriginName(profileOriginName)
                        .fileName(profileSavedFileName)
                        .filePath(profileFilePath)
                        .storeEntity(storeEntity)
                        .build();
                storeProfileFileRepository.save(savedStoreProfileFileEntity);
            }else{
                StoreProfileFileEntity savedStoreProfileFileEntity = StoreProfileFileEntity.builder()
                        .fileOriginName(profileOriginName)
                        .fileName(profileSavedFileName)
                        .filePath(profileFilePath)
                        .storeEntity(storeEntity)
                        .build();
                storeProfileFileRepository.save(savedStoreProfileFileEntity);
            }


            try {
                profile.transferTo(new File(profileFilePath));
            } catch (IOException e) {
                log.error("가게 프로필 사진 업데이트 실패");
            }
        }

        // 가게 메뉴

        // 기존 거 삭제
        Optional<StoreMenuFileEntity[]> storeMenuFileEntity = storeMenuFileRepository.findByStoreEntityId(storeRegisterRequestDto.getId());
        if(storeMenuFileEntity.isPresent()){
            String[] delFilePath = new String[storeMenuFileEntity.get().length];
            for(int i = 0; i<delFilePath.length; i++){
                delFilePath[i] = storeMenuFileEntity.get()[i].getFilePath();
            }

            deleteFileList(delFilePath);
            storeMenuFileRepository.deleteByStoreEntityId(storeRegisterRequestDto.getId());

        }

        // 새로 저장
        if(menus != null){
            ArrayList<StoreMenuFileEntity> storeMenuFileEntities = new ArrayList<>();

            for(MultipartFile menu : menus){
                String menuOriginName = menu.getOriginalFilename();
                UUID menuUuid = UUID.randomUUID();
                String menuSavedFileName = menuUuid.toString() + "_" + menuOriginName;
                String menuFilePath = uploadPath+menuSavedFileName;
                StoreMenuFileEntity storeMenuFileEntitySaved = StoreMenuFileEntity.builder()
                        .fileOriginName(menuOriginName)
                        .fileName(menuSavedFileName)
                        .filePath(menuFilePath)
                        .storeEntity(storeEntity)
                        .build();
                storeMenuFileEntities.add(storeMenuFileEntitySaved);
                try {
                    menu.transferTo(new File(menuFilePath));
                } catch (IOException e) {
                    log.error("메뉴 사진 등록 실패");
                }
            }
            storeMenuFileRepository.saveAll(storeMenuFileEntities);


        }

    }

    public void deleteFile(String filePath){
        File delFile = new File(filePath);
        if(delFile.isFile()){
            delFile.delete();
        }
    }

    public void deleteFileList(String[] filePathList){
        for(String path : filePathList){
            File delFile = new File(path);
            if(delFile.isFile()){
                delFile.delete();
            }

        }
    }

    public ResponseEntity<Resource> getProfile(long userId){
        Optional<StoreEntity> storeEntity = storeRepository.findByUserEntityId(userId);
        if(storeEntity.isPresent()){
            Optional<StoreProfileFileEntity> storeProfileFileEntity = storeProfileFileRepository.findByStoreEntityId(storeEntity.get().getId());
            if(storeProfileFileEntity.isPresent()){
                String filePathStr = storeProfileFileEntity.get().getFilePath();
                Resource resource = new FileSystemResource(filePathStr);

                if(!resource.exists()){
                    return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
                }
                HttpHeaders header = new HttpHeaders();
                Path filePath = null;
                try {
                    filePath = Paths.get(filePathStr);
                    header.add("Content-Type", Files.probeContentType(filePath));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
            }
        }
        return null;
    }

    public StoreRegisterResponseDto getStore(long userId){
        Optional<UserEntity> user = userRepository.findById(userId);
        Optional<StoreEntity> store = storeRepository.findByUserEntityId(user.get().getId());
        if(store.isPresent()){
            Optional<StoreMenuFileEntity[]> storeMenuFileEntity = storeMenuFileRepository.findByStoreEntityId(store.get().getId());
            ArrayList<String> menuFileNameList = new ArrayList<>();
            if(storeMenuFileEntity.isPresent()){
                for(StoreMenuFileEntity menuFile : storeMenuFileEntity.get()){
                    menuFileNameList.add(menuFile.getFileOriginName());
                }
            }
            StoreRegisterResponseDto storeRegisterResponseDto = StoreRegisterResponseDto.builder()
                    .id(store.get().getId())
                    .storeName(store.get().getStoreName())
                    .mainAddress(store.get().getMainAddress())
                    .detailAddress(store.get().getDetailAddress())
                    .holiday(store.get().getHoliday())
                    .startTime(store.get().getStartTime())
                    .endTime(store.get().getEndTime())
                    .introduce(store.get().getIntroduce())
                    .phoneNumber(store.get().getPhoneNumber())
                    .zipcode(store.get().getZipcode())
                    .menuFileNameList(menuFileNameList)
                    .build();
            return storeRegisterResponseDto;
        }
        return null;

    }
}
