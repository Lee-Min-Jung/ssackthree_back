package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/customer/profile")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(path = "/upload/{userId}")
    public void uploadProfile(@RequestParam("profile") MultipartFile file, @PathVariable(name="userId") long userId) throws IOException {
        customerService.uploadProfile(file, userId);
    }

    @PostMapping(path = "/update/{userId}")
    public void updateProfile(@RequestParam("profile") MultipartFile file, @PathVariable(name="userId") long userId) throws IOException {
        customerService.updateProfile(file, userId);
    }

    @GetMapping(path = "/show/{userId}")
    public ResponseEntity<Resource> getProfile(@PathVariable(name="userId") long userId) throws Exception{
        return customerService.getProfile(userId);
    }
}
