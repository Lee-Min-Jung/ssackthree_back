package com.ssackthree.ssackthree_back.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomRequestDto;
import com.ssackthree.ssackthree_back.entity.ChatMessageEntity;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.repository.ChatMessageRepository;
import com.ssackthree.ssackthree_back.repository.ChatRoomRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public void createChatRoom(ChatRoomRequestDto chatRoomDto){
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .userEntity1(userRepository.findById(chatRoomDto.getUserId1()).get())
                .userEntity2(userRepository.findById(chatRoomDto.getUserId2()).get())
                .createdDate(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoomEntity);
    }

    public void createChatMessage(ChatMessageRequestDto chatMessageRequestDto){
        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .sender(userRepository.findById(chatMessageRequestDto.getSenderId()).get())
                .receiver(userRepository.findById(chatMessageRequestDto.getReceiverId()).get())
                .content(chatMessageRequestDto.getContent())
                .chatRoomEntity(chatRoomRepository.findById(chatMessageRequestDto.getRoomId()).get())
                .createdDate(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessageEntity);
    }

}
