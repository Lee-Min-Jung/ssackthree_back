package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.KakaoPayApproveResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayReadyResponseDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayRequestDto;
import com.ssackthree.ssackthree_back.dto.KakaoPayResultResponseDto;
import com.ssackthree.ssackthree_back.entity.MenuEntity;
import com.ssackthree.ssackthree_back.entity.OrderEntity;
import com.ssackthree.ssackthree_back.entity.StoreEntity;
import com.ssackthree.ssackthree_back.repository.MenuRepository;
import com.ssackthree.ssackthree_back.repository.OrderRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    public KakaoPayReadyResponseDto payReady(KakaoPayRequestDto kakaoPayRequestDto){
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
        parameters.add("cancel_url", "http://localhost:8080/api/payment/kakaopay/cancel"); // 결제취소시 넘어갈 url
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
                .status("T")
                .menuEntity(menuRepository.findById(kakaoPayRequestDto.getMenuId()).get())
                .userEntity(userRepository.findById(kakaoPayRequestDto.getUserId()).get())
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
                    .build();

            orderRepository.save(savedOrder);
        }
    }

    public KakaoPayApproveResponseDto payApprove(String pgToken, String orderId){

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

        // 요청 보내기기
       RestTemplate restTemplate = new RestTemplate();

        KakaoPayApproveResponseDto approveResponse = restTemplate.postForObject(
                approveUrl,
                requestEntity,
                KakaoPayApproveResponseDto.class);

        // 주문 세부 정보 저장
        approveResponse.setKakaoPayResultResponseDto(getKakaoPayResultResponseDto(Long.parseLong(approveResponse.getPartner_order_id())));

        return approveResponse;

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
