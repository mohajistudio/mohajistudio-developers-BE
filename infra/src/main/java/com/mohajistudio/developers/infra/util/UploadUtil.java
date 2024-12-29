package com.mohajistudio.developers.infra.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UploadUtil {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String IMAGES_DIR = "media/images/";
    private static final String VIDEOS_DIR = "media/videos/";

    public static String createImagesFileName(String originalFileName, UUID memberId) {
        String ext = getFileExtension(originalFileName);
        UUID uuid = UuidCreator.getTimeOrdered();
        return IMAGES_DIR + memberId + "/" + uuid + ext;
    }

    public static String createVideosFileName(String originalFileName, UUID memberId) {
        String ext = getFileExtension(originalFileName);
        UUID uuid = UuidCreator.getTimeOrdered();
        return VIDEOS_DIR + memberId + "/" + uuid + ext;
    }

    private static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
    }
}