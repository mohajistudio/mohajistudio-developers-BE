package com.mohajistudio.developers.database.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TagDto {
    private UUID id;
    private String title;

    @QueryProjection
    public TagDto(UUID id, String title) {
        this.id = id;
        this.title = title;
    }
}
