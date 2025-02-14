package com.mohajistudio.developers.infra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

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

    public String generatePostMetadata(String content) {
        String processedContent = content.replace("{", "{{").replace("}", "}}");

        String promptContent = """
                다음은 개발자 블로그에 작성될 Markdown 블로그 글입니다.
                
                📌 **요청 사항**
                1️⃣ **이 Markdown 내용을 두 줄로 소개하는 개요를 작성해주세요.**
                2️⃣ **관련된 태그를 5개 추천해주세요 (쉼표 없이 리스트 형태).**
                3️⃣ **블로그 글에 대한 리뷰를 세 문장 정도의 길이로 작성해주세요.**
                   - **잘한 점**, **부족한 점**, **개선할 점**을 포함하여 자연스럽게 이어서 작성해주세요.
                   - 리스트 형태가 아닌 **하나의 문자열로 표현**해주세요.
                
                ✅ **출력 형식**
                - 결과는 반드시 **JSON 형식만 출력**해야 합니다.
                - 백틱(```)을 포함하지 말고, 순수 JSON 데이터만 반환하세요.
                - 개요의 경우 **summary** 키를 사용하고, 태그의 경우 **tags** 키를 사용하고, 리뷰의 경우 **review** 키를 사용하세요.

                📄 **Markdown 내용:**
                {markdownContent}
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("markdownContent", processedContent);

        PromptTemplate promptTemplate = new PromptTemplate(promptContent, variables);

        Prompt prompt = promptTemplate.create();

        return azureOpenAiChatModel.call(prompt).getResult().getOutput().getContent();
    }

    public Flux<String> streamChat(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create();

        return azureOpenAiChatModel.stream(prompt).map(chatMessage -> chatMessage.getResult().getOutput().getContent());
    }
}
