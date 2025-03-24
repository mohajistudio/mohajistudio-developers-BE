package com.mohajistudio.developers.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohajistudio.developers.common.dto.DiscordEmbedObjectDto;
import com.mohajistudio.developers.common.dto.DiscordFieldDto;
import com.mohajistudio.developers.common.dto.DiscordWebHookDto;
import com.mohajistudio.developers.common.utils.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordWebHookService {
    @Value("${logging.webhook.discord.error-url}")
    private String urlString;
    @Value("${logging.webhook.discord.avatar-url}")
    private String avatarUrl;

    @Async("threadPoolTaskExecutor")
    public void sendWebHookAsync(HttpServletRequest httpServletRequest, String statusCode, String errorCode, String errorMessage, String errorDescription) {
        DiscordFieldDto methodFieldDto = DiscordFieldDto.createInlineField("Method", httpServletRequest.getMethod());

        String uri = httpServletRequest.getRequestURI();
        String query = httpServletRequest.getQueryString();
        String fullUrl = query != null ? uri + "?" + query : uri;

        DiscordFieldDto endpointFieldDto = DiscordFieldDto.createInlineField("Endpoint", fullUrl);

        String accessToken = httpServletRequest.getHeader("Authorization");

        DiscordFieldDto accessTokenFieldDto = DiscordFieldDto.createField("AccessToken", accessToken == null ? "null" : accessToken);

        DiscordFieldDto clientIpFieldDto;
        try {
            clientIpFieldDto = DiscordFieldDto.createInlineField("ClientIp", HttpUtil.getClientIp(httpServletRequest));
        } catch(Exception e) {
            clientIpFieldDto = DiscordFieldDto.createInlineField("ClientIp", httpServletRequest.getRemoteAddr());
            log.error("Failed to get client IP", e);
        }

        DiscordFieldDto errorCodeFieldDto = DiscordFieldDto.createInlineField("ErrorCode", errorCode);
        DiscordFieldDto statusCodeFieldDto = DiscordFieldDto.createInlineField("StatusCode", statusCode);
        DiscordFieldDto errorMessageFieldDto = DiscordFieldDto.createField("ErrorMessage", errorMessage);

        DiscordEmbedObjectDto embedObjectDto = DiscordEmbedObjectDto.createErrorEmbedObject(List.of(methodFieldDto, endpointFieldDto, clientIpFieldDto, errorCodeFieldDto, statusCodeFieldDto, errorMessageFieldDto, accessTokenFieldDto));
        DiscordEmbedObjectDto embedObject2Dto = DiscordEmbedObjectDto.createErrorEmbedObject("ErrorMessage", errorDescription, new ArrayList<>());

        DiscordWebHookDto discordWebHookDto = DiscordWebHookDto.createErrorMessage(avatarUrl, "API Server Error", List.of(embedObjectDto, embedObject2Dto));

        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Discord-WebHook-Java");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(discordWebHookDto);

            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                stream.flush();

                connection.getInputStream().close();
                connection.disconnect();
            }
        } catch (IOException e) {
            log.error("Failed to send webhook", e);
        }
        CompletableFuture.completedFuture(null);
    }
}
