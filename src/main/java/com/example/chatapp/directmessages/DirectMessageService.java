package com.example.chatapp.directmessages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectMessageService {
    private final DirectMessageRepository directMessageRepository;
    public Optional<String> getDirectMessageId(
            String senderId,
            String recipientId,
            boolean createNewDirectMessageIfNotExists
    ) {
        return directMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(x -> x.getChatId())
                .or(() -> {
                    if (createNewDirectMessageIfNotExists){
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }
    @Transactional
    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        DirectMessage senderRecipient = DirectMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        DirectMessage recipientSender = DirectMessage.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        directMessageRepository.save(senderRecipient);
        directMessageRepository.save(recipientSender);
        return chatId;
    }
}
