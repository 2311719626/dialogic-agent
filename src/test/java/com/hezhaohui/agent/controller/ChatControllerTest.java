package com.hezhaohui.agent.controller;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ChatControllerTest {
    @Resource
    private ChatController chatController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testChat() {
        String result = chatController.chat("hello");
        System.out.println(result);
    }

    @Test
    public void testFileUpload() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.doc", "application/msword", "Hello, World!".getBytes());


        mockMvc.perform(multipart("/upload").file(mockMultipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.fileName").exists());

    }
}
