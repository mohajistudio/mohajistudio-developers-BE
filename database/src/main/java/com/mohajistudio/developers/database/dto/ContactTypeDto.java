package com.mohajistudio.developers.database.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ContactTypeDto {
    private UUID id;
    private String name;
    private String imageUrl;

    @QueryProjection
    public ContactTypeDto(UUID id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
