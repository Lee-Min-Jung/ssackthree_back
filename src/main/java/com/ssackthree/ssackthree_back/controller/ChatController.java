package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomRequestDto;
import com.ssackthree.ssackthree_back.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping(path = "/room")
    public int createChatRoom(@RequestBody ChatRoomRequestDto chatRoomDto){
        chatService.createChatRoom(chatRoomDto);
        return 1;
    }

//    @PostMapping(path = "/message")
//    public int createChatMessage(@RequestBody ChatMessageRequestDto chatMessageRequestDto){
//        chatService.createChatMessage(chatMessageRequestDto);
//        return 1;
//    }
}
