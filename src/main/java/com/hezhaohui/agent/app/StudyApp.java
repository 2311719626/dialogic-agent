package com.hezhaohui.agent.app;

import com.hezhaohui.agent.config.PromptConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Service
public class StudyApp {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    // Configuration for the prompt
    private final PromptConfig promptConfig;


    /**
     * 构造函数，初始化ChatClient和ChatMemory对象
     *
     * @param dashscopeChatModel ChatModel对象
     * @param promptConfig PromptConfig对象
     */
    public StudyApp(ChatModel dashscopeChatModel, PromptConfig promptConfig) {
        // 初始化promptConfig对象
        this.promptConfig = promptConfig;
        // 创建InMemoryChatMemory对象，用于存储聊天记录
        this.chatMemory = new InMemoryChatMemory();
        // 使用ChatClient.builder()方法创建ChatClient对象
        this.chatClient = buildChatClient(dashscopeChatModel);
    }

    /**
     * 构建ChatClient对象
     *
     * @param chatModel ChatModel对象
     * @return ChatClient对象
     */
    private ChatClient buildChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(promptConfig.getSystemPrompt())
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    /**
     * 处理聊天请求
     *
     * @param message 聊天消息
     * @param chatId 聊天会话ID
     * @return 聊天响应
     */
    public String doChat(String message, String chatId) {
        log.info("收到消息：{}", message);

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, promptConfig.getHistoryRetrieveSize()))
                .call()
                .chatResponse();
        String answer = chatResponse.getResult().getOutput().getText();

        log.info("响应消息：{}", answer);
        return answer;
    }

    public Flux<String> doChatFlux(String message, String chatId) {
        log.info("收到消息：{}", message);

        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, promptConfig.getHistoryRetrieveSize()))
                .stream()
                .content();
    }

}
