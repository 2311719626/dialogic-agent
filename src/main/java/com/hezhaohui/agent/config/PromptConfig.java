package com.hezhaohui.agent.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class PromptConfig {
    @Value("${chat.retrieve-size:10}")
    @Getter
    private int historyRetrieveSize;

    @Value("classpath:prompts/system-prompt.txt")
    private Resource promptResource;

    @Getter
    private String systemPrompt;

    @PostConstruct
    // 在Bean被初始化后执行
    public void init() throws IOException {
        // 读取promptResource文件的内容，并将其赋值给systemPrompt
        this.systemPrompt = Files.readString(promptResource.getFile().toPath());
    }
}
