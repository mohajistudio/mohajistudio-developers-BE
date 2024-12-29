package com.mohajistudio.developers.database.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContentType {

    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_WEBP("image/webp"),
    VIDEO_MP4("video/mp4"),
    VIDEO_MPEG("video/mpeg");

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ContentType fromMimeType(String mimeType) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported content type: " + mimeType));
    }

    public static boolean isValid(String mimeType) {
        return Arrays.stream(ContentType.values())
                .anyMatch(contentType -> contentType.getMimeType().equalsIgnoreCase(mimeType));
    }
}