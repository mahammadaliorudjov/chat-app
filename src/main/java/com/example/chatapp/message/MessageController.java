package com.example.chatapp.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        try {
            Message savedMessage = messageService.save(message);
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(), "/queue/messages",
                    ChatNotification.builder()
                            .id(savedMessage.getId())
                            .senderId(savedMessage.getSenderId())
                            .recipientId(savedMessage.getRecipientId())
                            .content(savedMessage.getContent())
                            .timestamp(savedMessage.getTimestamp())
                            .build()
            );
        } catch (MessageServiceException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Message>> findMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("recipientId") String recipientId
    ) {
        return ResponseEntity.ok(messageService.findMessages(senderId, recipientId));
    }
}
