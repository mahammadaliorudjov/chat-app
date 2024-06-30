package com.example.chatapp.message;

import com.example.chatapp.directmessages.DirectMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final DirectMessageService directMessageService;

    public Message save(Message message) throws MessageServiceException {
        var chatId = directMessageService.getDirectMessageId(
                message.getSenderId(),
                message.getRecipientId(),
                true
        ).orElseThrow(() -> new MessageServiceException());
        message.setChatId(chatId);
        message.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        messageRepository.save(message);
        return message;
    }
    public List<Message> findMessages(String senderId, String recipientId) {
        var chatId = directMessageService.getDirectMessageId(
                senderId,
                recipientId,
                true);
        return chatId.map(x -> messageRepository.findByChatIdOrderByTimestampAsc(x)).orElse(new ArrayList<>());
    }
}
