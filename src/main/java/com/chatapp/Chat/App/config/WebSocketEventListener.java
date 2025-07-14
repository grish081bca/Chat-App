package com.chatapp.Chat.App.config;

import com.chatapp.Chat.App.controller.ChatMessage;
import com.chatapp.Chat.App.controller.Messagetype;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        String userName = headerAccessor.getSessionAttributes().get("username").toString();

        if (userName != null) {
            log.info("user is disconnected : {}", userName);
            var chatMessage = ChatMessage.builder()
                    .type(Messagetype.LEAVE)
                    .sender(userName)
                    .build();
            simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
        log.info("Received a new web socket connection");
    }
}
