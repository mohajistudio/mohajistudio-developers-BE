package com.mohajistudio.developers.database.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ContactDto {
    private UUID id;
    private String name;
    private String imageUrl;
    private String displayName;
    private String url;

    @QueryProjection
    public ContactDto(UUID id, String name, String imageUrl, String displayName, String url) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.displayName = displayName;
        this.url = url;
    }
}
