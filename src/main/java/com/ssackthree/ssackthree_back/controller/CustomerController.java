package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.OrderBargainHistoryRequestDto;
import com.ssackthree.ssackthree_back.dto.OrderBargainHistoryResponseDto;
import com.ssackthree.ssackthree_back.dto.SetLocationRequestDto;
import com.ssackthree.ssackthree_back.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(path = "/profile/upload/{userId}")
    public void uploadProfile(@RequestParam("profile") MultipartFile file, @PathVariable(name="userId") long userId) throws IOException {
        customerService.uploadProfile(file, userId);
    }

    @PostMapping(path = "/profile/update/{userId}")
    public void updateProfile(@RequestParam("profile") MultipartFile file, @PathVariable(name="userId") long userId) throws IOException {
        customerService.updateProfile(file, userId);
    }

    @GetMapping(path = "/profile/show/{userId}")
    public String getProfile(@PathVariable(name="userId") long userId) throws Exception{
        return customerService.getProfile(userId);
    }


    @PostMapping(path = "/register/location")
    public void registerLocation(@RequestBody SetLocationRequestDto setLocationRequestDto) throws Exception {
        customerService.registerLocation(setLocationRequestDto);
    }

    @GetMapping(path = "/orderBargain/history")
    public List<OrderBargainHistoryResponseDto> getOrderBargainHistory(@RequestBody OrderBargainHistoryRequestDto orderBargainHistoryRequestDto) throws Exception{
        return customerService.getOrderBargainHistory(orderBargainHistoryRequestDto);
    }
}
