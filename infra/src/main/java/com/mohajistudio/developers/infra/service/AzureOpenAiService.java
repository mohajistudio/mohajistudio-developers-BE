package com.mohajistudio.developers.infra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureOpenAiService {
    private final AzureOpenAiChatModel azureOpenAiChatModel;

    public String chat(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create();

        return azureOpenAiChatModel.call(prompt).getResult().getOutput().getContent();
    }

    public String generateSummary(String content) {
        String processedContent = content.replace("{", "{{").replace("}", "}}");

        processedContent = processedContent.replace("`", "\\`");

        String promptContent = """
        다음은 개발자 블로그에 작성될 Markdown 블로그 글입니다.
        이 내용을 두 줄로 게시글을 소개하는 개요를 작성해주세요.

        ✅ 요약 스타일:
        - 핵심 정보만 포함
        - 간결하지만 정확한 표현 사용
        - 문장 연결이 자연스럽도록

        📌 Markdown 내용:
        """ + processedContent;

        PromptTemplate promptTemplate = new PromptTemplate(promptContent);

        Prompt prompt = promptTemplate.create();

        return azureOpenAiChatModel.call(prompt).getResult().getOutput().getContent();
    }

    public Flux<String> streamChat(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create();

        return azureOpenAiChatModel.stream(prompt)
                .map(chatMessage -> chatMessage.getResult().getOutput().getContent());
    }
}
