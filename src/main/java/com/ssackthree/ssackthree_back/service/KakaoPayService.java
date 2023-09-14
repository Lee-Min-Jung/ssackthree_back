package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.KakaoPayApproveResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayReadyResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayRequestDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayResultResponseDto;
import com.ssackthree.ssackthree_back.entity.*;
import com.ssackthree.ssackthree_back.enums.BargainStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.enums.OrderStatusEnum;
import com.ssackthree.ssackthree_back.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayService {

    @Value("${kakao.pay.admin-key}")
    private String admin;

    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.ready-url}")
    private String readyUrl;

    @Value("${kakao.pay.approve-url}")
    private String approveUrl;

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final MenuStatusRepository menuStatusRepository;
    private final BargainOrderRepository bargainOrderRepository;
    private final NotificationService notificationService;

    public KakaoPayReadyResponseDto payReady(KakaoPayRequestDto kakaoPayRequestDto){
        // 해당 메뉴 id로 결제 중인 것이 있는 지 확인
        if(orderRepository.findByMenuEntityId(kakaoPayRequestDto.getMenuId()).isPresent()){
            return null;
        }


        // orderEntity 생성
        OrderEntity order = saveOrderEntity(kakaoPayRequestDto);

        // 카카오페이 서버로 보내기 위한 준비
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", String.valueOf(order.getId()));
        parameters.add("partner_user_id", String.valueOf(kakaoPayRequestDto.getUserId()));
        parameters.add("item_name", kakaoPayRequestDto.getName());
        parameters.add("quantity", "1");
        parameters.add("total_amount", String.valueOf(kakaoPayRequestDto.getPrice()));
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "http://localhost:8080/api/payment/kakaopay/success"+"?orderId="+String.valueOf(order.getId())); // 결제승인시 넘어갈 url
        parameters.add("cancel_url", "http://localhost:8080/api/payment/kakaopay/cancel"+"?orderId="+String.valueOf(order.getId())); // 결제취소시 넘어갈 url
        parameters.add("fail_url", "http://localhost:8080/api/payment/kakaopay/fail"); // 결제 실패시 넘어갈 url
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 카카오페이 서버로 요청 보내기
        RestTemplate template = new RestTemplate();
        KakaoPayReadyResponseDto readyResponse = template.postForObject(readyUrl, requestEntity, KakaoPayReadyResponseDto.class);

        // tid 저장
        saveTid(order.getId(), readyResponse);


        return readyResponse;
    }

    public OrderEntity saveOrderEntity(KakaoPayRequestDto kakaoPayRequestDto){
        OrderEntity order = OrderEntity.builder()
                .status(OrderStatusEnum.READY)
                .menuEntity(menuRepository.findById(kakaoPayRequestDto.getMenuId()).get())
                .userEntity(userRepository.findById(kakaoPayRequestDto.getUserId()).get())
                .createdDate(LocalDateTime.now())
                .build();


        return orderRepository.save(order);
    }

    public void saveTid(long orderId, KakaoPayReadyResponseDto readyResponse){
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        if(order.isPresent()){
            OrderEntity savedOrder = OrderEntity.builder()
                    .id(order.get().getId())
                    .tid(readyResponse.getTid())
                    .status(order.get().getStatus())
                    .menuEntity(order.get().getMenuEntity())
                    .userEntity(order.get().getUserEntity())
                    .createdDate(order.get().getCreatedDate())
                    .build();

            orderRepository.save(savedOrder);
        }
    }

    public String payApprove(String pgToken, String orderId){

        // tid를 찾아와야 함
        OrderEntity order = orderRepository.findById(Long.parseLong(orderId)).get();

        // 요청 준비
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", order.getTid());
        parameters.add("partner_order_id", String.valueOf(order.getId()));
        parameters.add("partner_user_id", String.valueOf(order.getUserEntity().getId()));
        parameters.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 요청 보내기
       RestTemplate restTemplate = new RestTemplate();

        KakaoPayApproveResponseDto approveResponse = restTemplate.postForObject(
                approveUrl,
                requestEntity,
                KakaoPayApproveResponseDto.class);

        // 주문한 메뉴 상태 변경 - 흥정인지 아닌지 확인한 후 다르게 변경해줘야 함
        updateMenuStatus(approveResponse);

        // 결제 완료했으니 주문 상태 바꾸기
        updateOrderStatus(approveResponse);


        // 주문 세부 정보 저장
        approveResponse.setKakaoPayResultResponseDto(getKakaoPayResultResponseDto(Long.parseLong(approveResponse.getPartner_order_id())));

//        return approveResponse;

        // 결제 완료 알림
        notificationService.notify(order.getMenuEntity().getStoreEntity().getUserEntity().getId(), "결제 완료");


        return "https://www.naver.com/";

    }

    public void payCancel(String orderId){
        Optional<OrderEntity> order = orderRepository.findById(Long.parseLong(orderId));

        if(order.isPresent()){
            OrderEntity orderEntity = OrderEntity.builder()
                    .id(order.get().getId())
                    .userEntity(order.get().getUserEntity())
                    .menuEntity(order.get().getMenuEntity())
                    .tid(order.get().getTid())
                    .status(OrderStatusEnum.CANCEL)
                    .build();
            orderRepository.save(orderEntity);
        }
    }

    public void updateOrderStatus(KakaoPayApproveResponseDto approveResponse){
        long orderId = Long.parseLong(approveResponse.getPartner_order_id());
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderId);

        if(orderEntity.isPresent()){
            OrderEntity order = OrderEntity.builder()
                    .id(orderId)
                    .userEntity(orderEntity.get().getUserEntity())
                    .menuEntity(orderEntity.get().getMenuEntity())
                    .tid(orderEntity.get().getTid())
                    .status(OrderStatusEnum.COMPLETED)
                    .createdDate(orderEntity.get().getCreatedDate())
                    .build();
            orderRepository.save(order);
        }
    }

    public void updateMenuStatus(KakaoPayApproveResponseDto approveResponse){
        MenuEntity menuEntity = orderRepository.findById(Long.parseLong(approveResponse.getPartner_order_id())).get().getMenuEntity();

        // 흥정이 아닌 경우
        if(menuEntity.getIsBargainning().equals("F")){
            // 메뉴 상태 바꿈
            MenuStatusEntity menuStatusEntity = menuEntity.getMenuStatusEntity();
            MenuStatusEntity updatedMenuStatusEntity = MenuStatusEntity.builder()
                    .menuStatus(MenuStatusEnum.ORDER_COMPLETED)
                    .id(menuStatusEntity.getId())
                    .menuEntity(menuEntity)
                    .build();
            menuStatusRepository.save(updatedMenuStatusEntity);
        // 흥정인 경우
        }else{
            // 만약 흥정 제안 없이 바로 주문하는 경우
            if(menuEntity.getMenuStatusEntity().getMenuStatus().equals(MenuStatusEnum.BARGAIN_ING)){
                log.info("++++++++++++++++++++++++");
                log.info("흥정 제안 없이 바로 주문");
                MenuStatusEntity menuStatusEntity = menuEntity.getMenuStatusEntity();
                MenuStatusEntity updatedMenuStatusEntity = MenuStatusEntity.builder()
                        .menuStatus(MenuStatusEnum.ORDER_COMPLETED)
                        .id(menuStatusEntity.getId())
                        .menuEntity(menuEntity)
                        .build();
                menuStatusRepository.save(updatedMenuStatusEntity);
            }else{
                // 메뉴 상태 바꿈
                MenuStatusEntity menuStatusEntity = menuEntity.getMenuStatusEntity();
                MenuStatusEntity updatedMenuStatusEntity = MenuStatusEntity.builder()
                        .menuStatus(MenuStatusEnum.BARGAIN_COMPLETED)
                        .id(menuStatusEntity.getId())
                        .menuEntity(menuEntity)
                        .build();
                menuStatusRepository.save(updatedMenuStatusEntity);

                // 흥정 주문의 메뉴 상태 바꿈
                long menuId = menuEntity.getId();
                long userId = Long.parseLong(approveResponse.getPartner_user_id());
                Optional<BargainOrderEntity> bargainOrder = bargainOrderRepository.findByMenuEntityIdAndUserEntityId(menuId, userId);
                if(bargainOrder.isPresent()){
                    BargainOrderEntity updatedBargainOrder = BargainOrderEntity.builder()
                            .id(bargainOrder.get().getId())
                            .status(BargainStatusEnum.BARGAIN_COMPLETED)
                            .menuEntity(bargainOrder.get().getMenuEntity())
                            .userEntity(bargainOrder.get().getUserEntity())
                            .bargainPrice(bargainOrder.get().getBargainPrice())
                            .createdDate(bargainOrder.get().getCreatedDate())
                            .build();
                    bargainOrderRepository.save(updatedBargainOrder);
                }
            }


        }



    }

    public KakaoPayResultResponseDto getKakaoPayResultResponseDto(long orderId){
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        if(order.isPresent()){
            OrderEntity orderEntity = order.get();
            MenuEntity menuEntity = order.get().getMenuEntity();
            StoreEntity storeEntity = orderEntity.getMenuEntity().getStoreEntity();
            KakaoPayResultResponseDto kakaoPayResultResponseDto = KakaoPayResultResponseDto.builder()
                    .storeName(storeEntity.getStoreName())
                    .totalPrice(menuEntity.getDiscountedPrice())
                    .storeAddress(storeEntity.getMainAddress()+storeEntity.getDetailAddress())
                    .endTime(storeEntity.getEndTime())
                    .build();
            return kakaoPayResultResponseDto;
        }
        return null;
   }

    // header() 셋팅
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + admin);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }

}
