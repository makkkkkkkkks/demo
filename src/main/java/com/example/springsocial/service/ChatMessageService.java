package com.example.springsocial.service;


import com.example.springsocial.exception.ResourceNotFoundException;
import com.example.springsocial.model.chat.ChatMessage;
import com.example.springsocial.model.chat.MessageStatus;
import com.example.springsocial.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository repository;
    @Autowired
    private ChatRoomService chatRoomService;


    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);
        var messages = chatId.map(Id -> repository.findByChatId(java.util.Optional.of(Id))).orElse(new ArrayList<>());
        if (messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return messages;
    }

    public ChatMessage findById(String id) {
        return repository.findById(Long.parseLong(id)).map(chatMessage -> {
            chatMessage.setStatus(MessageStatus.DELIVERED);
            return repository.save(chatMessage);
        }).orElseThrow(() -> new ResourceNotFoundException("can't find message " + id));
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = repository.findByChatId(chatId);
        messages.stream().filter(sta -> sta.getStatus().equals(MessageStatus.RECEIVED))
                .forEach(s -> s.setStatus(MessageStatus.DELIVERED));
        messages.stream().forEach(m -> repository.save(m));


    }
}
