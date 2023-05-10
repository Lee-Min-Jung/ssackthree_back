package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.repository.CustomerProfileFileRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final UserRepository userRepository;
    private final CustomerProfileFileRepository customerProfileFileRepository;

    @Value("${upload-path}")
    private String uploadPath;

    @Transactional
    public void uploadProfile(MultipartFile file, long userId){
        Optional<UserEntity> user = userRepository.findById(userId);

        String originName = file.getOriginalFilename();
//        String extension = StringUtils.getFilenameExtension(originName);

        UUID uuid = UUID.randomUUID();
        String savedFileName = uuid.toString() + "_" + originName;
        String filePath = uploadPath+savedFileName;
        CustomerProfileFileEntity customerProfileFileEntity = CustomerProfileFileEntity.builder()
                .fileOriginName(originName)
                .fileName(savedFileName)
                .filePath(filePath)
                .userEntity(user.get())
                .build();
        customerProfileFileRepository.save(customerProfileFileEntity);

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("프로필 사진 등록 실패");
        }

    }

//    public void checkExtension(String extension) throws Exception{
//        String extensionPattern = "^\\S+.(?i)(png|PNG|jpg|JPG|jpeg|jpeg)$";
//        if(!Pattern.matches(extensionPattern, extension)){
//            throw new Exception("BAD EXTENSION FILE UPLOAD");
//        }
//    }

    public void updateProfile(MultipartFile file, long userId){
        String originName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String savedFileName = uuid.toString() + "_" + originName;
        String filePath = uploadPath+savedFileName;

        Optional<CustomerProfileFileEntity> customerProfileFileEntity = customerProfileFileRepository.findByUserEntityId(userId);
        if(customerProfileFileEntity.isPresent()){
            String delFilePath = customerProfileFileEntity.get().getFilePath();
            deleteProfile(delFilePath);
            CustomerProfileFileEntity customerProfileFileEntitySaving = CustomerProfileFileEntity.builder()
                    .id(customerProfileFileEntity.get().getId())
                    .fileOriginName(originName)
                    .fileName(savedFileName)
                    .filePath(filePath)
                    .userEntity(customerProfileFileEntity.get().getUserEntity())
                    .build();

            customerProfileFileRepository.save(customerProfileFileEntitySaving);
        }
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("fail to store file : name={}, exception={}",
                    file.getOriginalFilename(),
                    e.getMessage());
        }


    }

    public void deleteProfile(String filePath){
        File delFile = new File(filePath);
        if(delFile.isFile()){
            delFile.delete();
        }
    }

    public ResponseEntity<Resource> getProfile(long userId){
        Optional<CustomerProfileFileEntity> customerProfileFileEntity = customerProfileFileRepository.findByUserEntityId(userId);
        if(customerProfileFileEntity.isPresent()){
            String filePathStr = customerProfileFileEntity.get().getFilePath();
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
        return null;
    }
}
