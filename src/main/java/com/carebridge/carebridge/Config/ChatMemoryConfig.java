package com.carebridge.carebridge.Config;

import com.carebridge.carebridge.Repository.SafeChatMemoryRepo;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatMemoryConfig {
    @Bean
    @Primary
    public ChatMemoryRepository safeChatMemoryRepository(
            MongoChatMemoryRepository mongoRepository) {

        return new SafeChatMemoryRepo(mongoRepository);
    }

    @Bean
    public ChatMemory chatMemory(
            ChatMemoryRepository repository) {

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }
}
