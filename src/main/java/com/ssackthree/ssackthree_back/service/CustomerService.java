package com.ssackthree.ssackthree_back.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.ssackthree.ssackthree_back.dto.SetLocationRequestDto;
import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.StoreLocationEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.entity.UserLocationEntity;
import com.ssackthree.ssackthree_back.repository.CustomerProfileFileRepository;
import com.ssackthree.ssackthree_back.repository.UserLocationRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import com.ssackthree.ssackthree_back.util.FileService;
import com.ssackthree.ssackthree_back.util.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final UserRepository userRepository;
    private final CustomerProfileFileRepository customerProfileFileRepository;
    private final FileService fileService;
    private final UserLocationRepository userLocationRepository;
    private final LocationService locationService;



    @Transactional
    public void uploadProfile(MultipartFile file, long userId) throws IOException {
        String savedFileName = fileService.getSavedFileName(file);

        // s3에 파일 업로드
        fileService.uploadFile(file, savedFileName);

        // DB에 내용 저장
        Optional<UserEntity> user = userRepository.findById(userId);

        CustomerProfileFileEntity customerProfileFileEntity = CustomerProfileFileEntity.builder()
                .fileOriginName(file.getOriginalFilename())
                .fileName(savedFileName)
                .filePath(fileService.getUrl(savedFileName))
                .userEntity(user.get())
                .build();
        customerProfileFileRepository.save(customerProfileFileEntity);



    }



    public void updateProfile(MultipartFile file, long userId) throws IOException {
        String savedFileName = fileService.getSavedFileName(file);

        // S3 저장
        fileService.uploadFile(file, savedFileName);

        // DB 수정

        Optional<CustomerProfileFileEntity> customerProfileFileEntity = customerProfileFileRepository.findByUserEntityId(userId);
        if(customerProfileFileEntity.isPresent()){
            // 기존 파일 삭제
            String delFileName = customerProfileFileEntity.get().getFileName();
            deleteProfile(delFileName);

            // 수정
            CustomerProfileFileEntity customerProfileFileEntitySaving = CustomerProfileFileEntity.builder()
                    .id(customerProfileFileEntity.get().getId())
                    .fileOriginName(file.getOriginalFilename())
                    .fileName(savedFileName)
                    .filePath(fileService.getUrl(savedFileName))
                    .userEntity(customerProfileFileEntity.get().getUserEntity())
                    .build();

            customerProfileFileRepository.save(customerProfileFileEntitySaving);
        }

    }

    public void deleteProfile(String filePath){
        fileService.deleteFile(filePath);
    }

    public String getProfile(long userId){
        Optional<CustomerProfileFileEntity> customerProfileFileEntity = customerProfileFileRepository.findByUserEntityId(userId);
        if(customerProfileFileEntity.isPresent()){
            return customerProfileFileEntity.get().getFilePath();
        }
        return null;
    }

    public void registerLocation(SetLocationRequestDto setLocationRequestDto) throws Exception {
        LatLng location = locationService.getLocation(setLocationRequestDto.getAddress());
        if(location != null){
            double latitude = location.lat;
            double longitude = location.lng;
            UserLocationEntity userLocationEntity = UserLocationEntity.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .m(setLocationRequestDto.getM())
                    .userEntity(userRepository.findById(setLocationRequestDto.getUserId()).get())
                    .build();
            userLocationRepository.save(userLocationEntity);
        }
    }


}
