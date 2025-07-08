package com.hezhaohui.agent.controller;

import com.hezhaohui.agent.service.FileStorageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class ChatController {
    // Spring Boot Bean
    private final ChatClient chatClient;
    private final FileStorageService fileStorageService;

    public ChatController(ChatClient.Builder builder, FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.chatClient = builder.build();
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String input) {
        return chatClient.prompt()
                .user(input)
                .call()
                .content();
    }

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        String storedFileName = fileStorageService.storeFile(file);
        return Map.of(
                "status", "success",
                "fileName", storedFileName
        );
    }
}
