package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.LocationDto;
import com.ssackthree.ssackthree_back.dto.MenuInDistanceResponseDto;
import com.ssackthree.ssackthree_back.service.customizedClass.MenuIdDistance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class MenuServiceTest {
    @Autowired
    private MenuService menuService;

    @Test
    @DisplayName("두 점 사이 거리 얻기 테스트")
    public void 거리() throws Exception{
        // given
        double lat1 = 37.25044159999999;
        double log1 = 127.0208851;
        double lat2 = 37.25193489999999;
        double log2 = 127.0339108;




        // when
        double dis = menuService.getDistance(lat1, log1, lat2, log2);

        //then
        System.out.println("************");
        System.out.println(dis);
    }

    @Test
    @DisplayName("반경 n미터 메뉴 아이디 구하기")
    public void 반경() throws Exception{
        // given
        LocationDto locationDto = LocationDto.builder()
                .latitude(37.5454821)
                .longitude(126.9723012)
                .km(1000)
                .build();

//        locationDto.setLongitude(127.0208851);
//        locationDto.setLatitude(37.25044159999999);



        //when
//        List<MenuIdDistance> idDistanceList = menuService.getMenuIdDistance(locationDto);

        //then
//        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//        if(idDistanceList.isEmpty()){
//            System.out.println("비어있음");
//        }else{
//            for(MenuIdDistance id : idDistanceList){
//                System.out.println("id");
//                System.out.println(id.getId());
//                System.out.println("거리");
//                System.out.println(id.getDistance());
//            }
//        }

    }

    @Test
    @DisplayName("반경 n미터 메뉴 리스트")
    public void 홈페이지메뉴() throws Exception{
        //given
        LocationDto locationDto = LocationDto.builder()
                .latitude(37.5454821)
                .longitude(126.9723012)
                .km(1000)
                .build();
        //when
//        List<MenuInDistanceResponseDto> menuInDistanceResponseDtoList = menuService.getMenuListInDistance(locationDto, "s");

        //then
//        for(MenuInDistanceResponseDto menuInDistanceResponseDto : menuInDistanceResponseDtoList){
//            System.out.println("이름: " + menuInDistanceResponseDto.getName());
//            System.out.println("가게이름: " + menuInDistanceResponseDto.getStoreName());
//            System.out.println("원래 가격: " + menuInDistanceResponseDto.getOriginalPrice());
//            System.out.println("할인 가격: " + menuInDistanceResponseDto.getDiscountedPrice());
//            System.out.println("거리: " + menuInDistanceResponseDto.getDistance());
//            System.out.println("아이디 : " + menuInDistanceResponseDto.getMenuId());
//
//
//        }
    }
}