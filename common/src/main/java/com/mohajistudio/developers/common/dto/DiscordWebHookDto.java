package com.mohajistudio.developers.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class DiscordWebHookDto {
    private String content;
    private boolean tts;
    private List<DiscordEmbedObjectDto> embeds;
    private String avatar_url;

    public static DiscordWebHookDto createErrorMessage(String avatarUrl, String content, List<DiscordEmbedObjectDto> embeds) {
        return DiscordWebHookDto.builder().avatar_url(avatarUrl).content(content).embeds(embeds).build();
    }
}
