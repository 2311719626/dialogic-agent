package com.hezhaohui.agent.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class StudyAppTest {

    @Autowired
    private StudyApp studyApp;

    @Test
    void chatForTest() {
        String answer = studyApp.doChat("如何求圆的面积？");

        assertThat(answer)
                .as("响应应包含教育指导内容")
                .containsAnyOf("提示", "思考", "面积公式", "π");
    }
}
