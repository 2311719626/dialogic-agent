package com.hezhaohui.agent.controller;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatControllerTest {
    @Resource
    private ChatController chatController;

    @Test
    public void testChat() {
        String result = chatController.chat("你好");
        System.out.println(result);
    }
}
