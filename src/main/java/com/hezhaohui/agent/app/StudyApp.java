package com.hezhaohui.agent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Service
public class StudyApp {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private static final String SYSTEM_PROMPT = """                                                                    
        你是一个叫 Wonder 的AI学习助手，致力于帮助学生以高效且有趣的方式掌握知识。                                   
        你的角色是引导者，旨在激发学生的自主思考能力，而非直接提供答案。                                             
                                                                                                                     
        # 基本准则                                                                                                   
        ...（内容）...                                                                                               
                                                                                                                     
        # 交互流程                                                                                                   
        1. 学生提出问题或请求帮助。                                                                                  
        2. 你首先询问学生已经做了哪些尝试。                                                                          
        3. 根据学生描述，提供适合当前水平的提示。                                                                    
        4. 如果学生回答不完整，通过追问引导至完整答案。                                                              
        5. 在结束时，总结学习要点，并给出主动学习提示（如“试着编一道类似题目”）。                                    
                                                                                                                     
                                                                                                                     
        记住：你的最终目标是让学生获得“啊哈时刻”（顿悟体验）！                                                       
        """;


    /**
     * 构造函数，用于初始化StudyApp对象
     *
     * @param dashscopeChatModel
     */
    public StudyApp(ChatModel dashscopeChatModel) {
        // 创建InMemoryChatMemory对象，用于存储聊天记录
        this.chatMemory = new InMemoryChatMemory();
        // 使用ChatClient.builder()方法创建ChatClient对象
        chatClient = ChatClient.builder(dashscopeChatModel)
                // 设置默认的系统提示
                .defaultSystem(SYSTEM_PROMPT)
                // 设置默认的聊天记忆
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                // 构建ChatClient对象
                .build();
    }

    /**
     * 处理聊天请求
     *
     * @param message 聊天消息
     * @return 聊天响应
     */
    public String doChat(String message, String chatId) {
        log.info("收到消息：{}", message);

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String answer = chatResponse.getResult().getOutput().getText();
        log.info("响应消息：{}", answer);
        return answer;
    }
}
