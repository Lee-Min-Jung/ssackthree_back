package com.ssackthree.ssackthree_back.service;

import com.ssackthree.ssackthree_back.dto.ChatRoomDto;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.repository.ChatRoomRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void createChatRoom(ChatRoomDto chatRoomDto){
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .userEntity1(userRepository.findById(chatRoomDto.getUserId1()).get())
                .userEntity2(userRepository.findById(chatRoomDto.getUserId2()).get())
                .createdDate(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoomEntity);
    }
}
