package com.carebridge.carebridge.Repository;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;

import java.util.List;

public class SafeChatMemoryRepo implements ChatMemoryRepository {
    private final MongoChatMemoryRepository delegate;

    public SafeChatMemoryRepo(
            MongoChatMemoryRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return delegate.findByConversationId(conversationId);
    }

    @Override
    public void saveAll(
            String conversationId,
            List<Message> messages) {

        List<Message> safeMessages = new ArrayList<>();

        for (Message message : messages) {
            if (message instanceof AssistantMessage assistant) {
                safeMessages.add(
                        new AssistantMessage(assistant.getText()));
            } else {
                safeMessages.add(message);
            }
        }

        delegate.saveAll(conversationId, safeMessages);
    }

    @Override
    public List<String> findConversationIds() {
        return delegate.findConversationIds();
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        delegate.deleteByConversationId(conversationId);
    }


}
