package com.hezhaohui.agent.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class StudyAppTest {

    @Autowired
    private StudyApp studyApp;

    @Test
    void chatForTest() {
        String chatId = UUID.randomUUID().toString();

        // Conversation 1
        String message = "如何求圆的面积？";
        String answer = studyApp.doChat(message, chatId);
        assertThat(answer)
                .as("响应应包含教育指导内容")
                .containsAnyOf("提示", "思考", "面积", "π");

        // Conversation 2 - 跟进相同主题
        message = "如果半径是5，这个圆的面积是多少？";
        answer = studyApp.doChat(message, chatId);
        assertThat(answer)
                .as("响应应引导自主计算")
                .containsAnyOf("自己", "公式", "过程")
                .as("应避免直接答案")
                .doesNotContain("78.5");

        // Conversation 3 - 新主题测试
        message = "如何计算三角形的面积？";
        answer = studyApp.doChat(message, chatId);
        assertThat(answer)
                .as("应重置话题上下文")
                .doesNotContain("圆");
    }
}
