package com.ssackthree.ssackthree_back.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssackthree.ssackthree_back.dto.ChatListResponseDto;
import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomRequestDto;
import com.ssackthree.ssackthree_back.dto.ChatRoomResponseDto;
import com.ssackthree.ssackthree_back.entity.ChatMessageEntity;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.entity.CustomerProfileFileEntity;
import com.ssackthree.ssackthree_back.entity.UserEntity;
import com.ssackthree.ssackthree_back.enums.RoleEnum;
import com.ssackthree.ssackthree_back.repository.ChatMessageRepository;
import com.ssackthree.ssackthree_back.repository.ChatRoomRepository;
import com.ssackthree.ssackthree_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 채팅 방 생성
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

    // 채팅 메시지 저장
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

    // 채팅방 목록
    public List<ChatListResponseDto> showChatRoomList(long userId){
        List<ChatListResponseDto> chatListResponseDtoList = new ArrayList<>();

        // 유저가 속해있는 모든 채팅방을 리턴함.
        List<ChatRoomEntity> chatRoomEntityList = chatRoomRepository.findAllChatRoomById(userId).get();
        for(ChatRoomEntity cr : chatRoomEntityList){
            UserEntity counterpart = cr.getUserEntity1().getId() == userId ? cr.getUserEntity2() : cr.getUserEntity1();
            // 채팅방에 메시지가 1개라도 있는 경우
            if(!cr.getChatMessageEntityList().isEmpty()){
                // 상대가 점주일때
                if(counterpart.getRole().equals(RoleEnum.ROLE_STORE)){
                    ChatListResponseDto chatListResponseDto = ChatListResponseDto.builder()
                            .counterpartName(counterpart.getStoreEntity().getStoreName())
                            .counterpartProfile(counterpart.getStoreEntity().getStoreProfileFileEntity().getFilePath())
                            .counterpartRole("점주")
                            .build();
                    chatListResponseDtoList.add(chatListResponseDto);
                }else{// 상대가 일반 손님일때
                    ChatListResponseDto chatListResponseDto = ChatListResponseDto.builder()
                            .counterpartName(counterpart.getRepName())
                            .counterpartProfile(Optional.ofNullable(counterpart.getCustomerProfileFileEntity()).map(CustomerProfileFileEntity::getFilePath).orElse(null))
                            .counterpartRole("사용자")
                            .build();
                    chatListResponseDtoList.add(chatListResponseDto);
                }



            }
        }

        return chatListResponseDtoList;

    }


}
