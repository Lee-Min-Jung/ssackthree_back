package com.ssackthree.ssackthree_back.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomResponseDto;
import com.ssackthree.ssackthree_back.entity.ChatMessageEntity;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
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
    static ChatRoomEntity chatRoomEntity;

    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto chatRoomDto){


        UserEntity user1 = userRepository.findById(chatRoomDto.getUserId1()).get();
        UserEntity user2 = userRepository.findById(chatRoomDto.getUserId2()).get();

        // 이미 둘 사이에 채팅 방이 존재
        if(chatRoomRepository.findByUserEntity1IdAndUserEntity2Id(user1.getId(), user2.getId()).isPresent()){
            chatRoomEntity = chatRoomRepository.findByUserEntity1IdAndUserEntity2Id(user1.getId(), user2.getId()).get();
        }else{ // 둘 사이에 채팅 방이 없음
            chatRoomEntity = ChatRoomEntity.builder()
                    .userEntity1(user1)
                    .userEntity2(user2)
                    .createdDate(LocalDateTime.now())
                    .build();

            chatRoomRepository.save(chatRoomEntity);
        }


        ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
                .roomId(chatRoomEntity.getId())
                .userId1(chatRoomDto.getUserId1())
                .userId2(chatRoomDto.getUserId2())
                .build();

        return chatRoomResponseDto;
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
