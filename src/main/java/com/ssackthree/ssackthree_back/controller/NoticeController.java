package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.NoticeResponseDto;
import com.ssackthree.ssackthree_back.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list/{userId}")
    public List<NoticeResponseDto> noticeList(@PathVariable(name = "userId") long userId){
        return noticeService.getNoticeList(userId);
    }
}
