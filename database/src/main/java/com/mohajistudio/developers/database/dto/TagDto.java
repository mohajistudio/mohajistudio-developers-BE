package com.mohajistudio.developers.database.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private UUID id;
    private String title;
    private String slug;
    private String description;
}
