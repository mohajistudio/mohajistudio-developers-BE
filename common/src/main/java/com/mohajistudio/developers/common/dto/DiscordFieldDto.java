package com.mohajistudio.developers.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class DiscordFieldDto {
    String name;
    String value;
    boolean inline;

    public static DiscordFieldDto createInlineField(String name, String value) {
        return DiscordFieldDto.builder().name(name).value(value).inline(true).build();
    }

    public static DiscordFieldDto createField(String name, String value) {
        return DiscordFieldDto.builder().name(name).value(value).build();
    }
}
