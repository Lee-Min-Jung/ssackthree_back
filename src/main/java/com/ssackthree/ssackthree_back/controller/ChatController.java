package com.ssackthree.ssackthree_back.controller;

import com.ssackthree.ssackthree_back.dto.*;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(path = "/room/list/{userId}")
    public List<ChatListResponseDto> showChatRoomList(@PathVariable(name="userId") long userId){
        return chatService.showChatRoomList(userId);
    }

    @GetMapping(path = "/content/{roomId}")
    public List<ChatMessageListResponseDto> chowChatMessageList(@PathVariable(name = "roomId") long roomId){
        return chatService.showChatMessageList(roomId);
    }

//    @PostMapping(path = "/message")
//    public int createChatMessage(@RequestBody ChatMessageRequestDto chatMessageRequestDto){
//        chatService.createChatMessage(chatMessageRequestDto);
//        return 1;
//    }
}
