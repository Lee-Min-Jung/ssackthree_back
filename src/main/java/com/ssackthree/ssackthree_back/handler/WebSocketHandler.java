package com.ssackthree.ssackthree_back.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssackthree.ssackthree_back.dto.ChatMessageRequestDto;
import com.ssackthree.ssackthree_back.entity.ChatMessageEntity;
import com.ssackthree.ssackthree_back.entity.ChatRoomEntity;
import com.ssackthree.ssackthree_back.repository.ChatRoomRepository;
import com.ssackthree.ssackthree_back.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getHandshakeHeaders().getFirst("userId");
        log.info(userId);
        sessions.put(userId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 들어온 메세지 확인
        String payload = message.getPayload();
        log.info("{}", payload);
        ChatMessageRequestDto chatMessageRequestDto = objectMapper.readValue(payload, ChatMessageRequestDto.class);

        // 메시지 보낼 상대 세션 찾기
        WebSocketSession recipientSession = sessions.get(String.valueOf(chatMessageRequestDto.getReceiverId()));

        // 상대 세션 존재
        if(recipientSession != null && recipientSession.isOpen()){
            // 상대에게 메시지 보내기
            try {
                recipientSession.sendMessage(new TextMessage(chatMessageRequestDto.getContent()));
            } catch (IOException e) {
                log.error("Failed to send message to recipient: {}", chatMessageRequestDto.getReceiverId(), e);
            }
            // 받은 메시지 db 저장
            chatService.createChatMessage(chatMessageRequestDto);
        }else{
            // 받은 메시지 db 저장
            chatService.createChatMessage(chatMessageRequestDto);
        }





    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String userId = session.getHandshakeHeaders().getFirst("userId");
//        sessions.remove(userId);
    }

}
