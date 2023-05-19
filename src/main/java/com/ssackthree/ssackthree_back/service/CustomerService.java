package com.ssackthree.ssackthree_back.service;

import com.google.maps.model.LatLng;
import com.ssackthree.ssackthree_back.dto.OrderBargainHistoryRequestDto;
import com.ssackthree.ssackthree_back.dto.OrderBargainHistoryResponseDto;
import com.ssackthree.ssackthree_back.dto.SetLocationRequestDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.repository.*;
import com.ssackthree.ssackthree_back.util.FileService;
import com.ssackthree.ssackthree_back.util.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final OrderRepository orderRepository;
    private final BargainOrderRepository bargainOrderRepository;

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

    public List<OrderBargainHistoryResponseDto> getOrderBargainHistory(OrderBargainHistoryRequestDto orderBargainHistoryRequestDto){
        long userId = orderBargainHistoryRequestDto.getUserId();
        String type = orderBargainHistoryRequestDto.getType();
        List<OrderBargainHistoryResponseDto> orderBargainHistoryResponseDtoList = new ArrayList<>();

        // 흥정 아닌 경우
        if(type.equals("order")){
            List<OrderEntity> orderEntityList = userRepository.findById(userId).get().getOrderEntityList();
            for(OrderEntity order : orderEntityList){
                if(order.getMenuEntity().getMenuStatusEntity().getMenuStatus().equals(MenuStatusEnum.ORDER_ING) || order.getMenuEntity().getMenuStatusEntity().getMenuStatus().equals(MenuStatusEnum.ORDER_COMPLETED)){
                    OrderBargainHistoryResponseDto orderBargainHistoryResponseDto = OrderBargainHistoryResponseDto.builder()
                            .menuName(order.getMenuEntity().getName())
                            .storeName(order.getMenuEntity().getStoreEntity().getStoreName())
                            .originalPrice(order.getMenuEntity().getOriginalPrice())
                            .discountedPrice(order.getMenuEntity().getDiscountedPrice())
                            .status(order.getStatus().toString())
                            .build();
                    orderBargainHistoryResponseDtoList.add(orderBargainHistoryResponseDto);
                }

            }
        // 흥정인 경우
        }else{
            List<BargainOrderEntity> bargainOrderEntityList = userRepository.findById(userId).get().getBargainOrderEntityList();
            for(BargainOrderEntity bargainOrder : bargainOrderEntityList){
                OrderBargainHistoryResponseDto orderBargainHistoryResponseDto = OrderBargainHistoryResponseDto.builder()
                        .menuName(bargainOrder.getMenuEntity().getName())
                        .storeName(bargainOrder.getMenuEntity().getStoreEntity().getStoreName())
                        .originalPrice(bargainOrder.getMenuEntity().getOriginalPrice())
                        .discountedPrice(bargainOrder.getBargainPrice())
                        .status(bargainOrder.getStatus().toString())
                        .build();
                orderBargainHistoryResponseDtoList.add(orderBargainHistoryResponseDto);
            }
        }
        return orderBargainHistoryResponseDtoList;
    }


}
