package com.example.chatapp.directmessages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, String> {

    Optional<DirectMessage> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
