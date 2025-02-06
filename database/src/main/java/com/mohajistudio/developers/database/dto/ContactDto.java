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
public class ContactDto {
    private UUID id;
    private String name;
    private String imageUrl;
    private String displayName;
    private String url;
}
