package com.ssackthree.ssackthree_back.util;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${google.api.key}")
    private String apiKey;

    public static final double EARTH_RADIUS = 6371.0088; // 지구 반지름 상수 선언

    // 주소 가지고 위도 경도 구하기
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

    // 위도 경도 간 거리 구하기
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d =EARTH_RADIUS* c * 1000;
        return d;
    }
}
