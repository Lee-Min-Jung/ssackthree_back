package com.ssackthree.ssackthree_back.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // 파일을 받아서 저장될 파일 이름을 리턴
    public String getSavedFileName(MultipartFile file){
        String originName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String savedFileName = uuid.toString() + "_" + originName;

        return savedFileName;
    }

    // s3로 파일 저장
    public void uploadFile(MultipartFile file, String savedFileName) throws IOException {
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(file.getInputStream().available());

        amazonS3.putObject(bucket, savedFileName, file.getInputStream(), objMeta);




    }


    // 저장된 파일 경로 가져오기
    public String getUrl(String savedFileName){
        return amazonS3.getUrl(bucket, savedFileName).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileName){
        amazonS3.deleteObject(bucket, fileName);
    }



}
