package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomResponseDto;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
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
    public ChatRoomResponseDto createChatRoom(@RequestBody ChatRoomRequestDto chatRoomDto){
        return chatService.createChatRoom(chatRoomDto);
    }

//    @PostMapping(path = "/message")
//    public int createChatMessage(@RequestBody ChatMessageRequestDto chatMessageRequestDto){
//        chatService.createChatMessage(chatMessageRequestDto);
//        return 1;
//    }
}
