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
    private Long tagCount;

    @QueryProjection
    public TagDto(UUID id, String title, long tagCount) {
        this.id = id;
        this.title = title;
        this.tagCount = tagCount;
    }

    @QueryProjection
    public TagDto(UUID id, String title, int tagCount) {
        this.id = id;
        this.title = title;
        this.tagCount = (long) tagCount;
    }

    @QueryProjection
    public TagDto(UUID id, String title) {
        this.id = id;
        this.title = title;
        this.tagCount = null;
    }
}
