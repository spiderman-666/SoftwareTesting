package com.tongji.wordtrail.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AiSentenceService {

    private final OpenAIClient openAIClient;
    private final String deploymentName;

    public AiSentenceService(@Value("${azure.openai.api.key}") String apiKey,
                             @Value("${azure.openai.endpoint}") String endpoint,
                             @Value("${azure.openai.deployment.name}") String deploymentName) {
        this.openAIClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();
        this.deploymentName = deploymentName;
    }
    public String generateExampleSentence(String language, String word) {
        // 构建温和提示词
        String prompt = String.format(
            "Please write an example sentence in %s using the word '%s', and also provide its Simplified Chinese translation. Separate the two parts with a '|'.",
            language, word
        );

        // 构建消息
        List<ChatRequestMessage> chatMessages = Arrays.asList(
                new ChatRequestSystemMessage("You are a helpful assistant."),
                new ChatRequestUserMessage(prompt)
        );

        // 配置请求选项
        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages)
                .setMaxTokens(256)
                .setTemperature(0.7)
                .setTopP(1.0);

        // 调用 Azure OpenAI API
        ChatCompletions chatCompletions = openAIClient.getChatCompletions(deploymentName, options);

        // 解析返回结果
        for (ChatChoice choice : chatCompletions.getChoices()) {
            String content = choice.getMessage().getContent().trim();
            // 确保返回内容符合格式
            System.out.println(content);
            String cleanedContent = content.replaceAll("\\s*\\|\\s*", "|").trim();
            if (cleanedContent.matches(".*\\|.*")) {
                return cleanedContent;
            }
        }
        return "Invalid response format.";
    }

    // 修改 generateStory 方法中的提示词和正则表达式
    public String generateStory(String language, List<String> words) {
        // 构建温和提示词
        String prompt = String.format(
            "Please write a short story in %s using the following words: %s. Then provide its Simplified Chinese translation. Separate the story and the translation with a '|'.",
            language, String.join(", ", words)
        );

        // 构建消息
        List<ChatRequestMessage> chatMessages = Arrays.asList(
                new ChatRequestSystemMessage("You are a creative storyteller."),
                new ChatRequestUserMessage(prompt)
        );

        // 配置请求选项
        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages)
                .setMaxTokens(2048)
                .setTemperature(0.8)
                .setTopP(1.0);

        // 调用 Azure OpenAI API
        ChatCompletions chatCompletions = openAIClient.getChatCompletions(deploymentName, options);

        // 解析返回结果
        for (ChatChoice choice : chatCompletions.getChoices()) {
            String content = choice.getMessage().getContent().trim();
            // 确保返回内容符合格式
            System.out.println(content);
            String cleanedContent = content.replaceAll("\\s*\\|\\s*", "|").trim();
            // 确保返回内容符合格式
            if (cleanedContent.matches("(?s).*\\|.*")) {
                return cleanedContent;
            }
        }
        return "Invalid response format.";
    }
}