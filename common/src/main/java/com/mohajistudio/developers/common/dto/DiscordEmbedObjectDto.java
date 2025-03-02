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
public class DiscordEmbedObjectDto {
    private String title;
    private String description;
    private String color;
    private List<DiscordFieldDto> fields;

    public static DiscordEmbedObjectDto createErrorEmbedObject(List<DiscordFieldDto> fields) {
        return DiscordEmbedObjectDto.builder().fields(fields).color("15548997").build();
    }

    public static DiscordEmbedObjectDto createErrorEmbedObject(String title, String description, List<DiscordFieldDto> fields) {
        return DiscordEmbedObjectDto.builder().title(title).description(description).fields(fields).color("15548997").build();
    }
}
